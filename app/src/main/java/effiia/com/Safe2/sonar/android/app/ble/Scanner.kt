/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.ble

import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import android.util.Base64
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.polidea.rxandroidble2.LogConstants
import com.polidea.rxandroidble2.LogOptions
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import timber.log.Timber
import effiia.com.Safe2.sonar.android.app.BuildConfig
import effiia.com.Safe2.sonar.android.app.crypto.BluetoothIdentifier
import effiia.com.Safe2.sonar.android.app.di.module.BluetoothModule
import javax.inject.Inject
import javax.inject.Named

class Scanner @Inject constructor(
    private val rxBleClient: RxBleClient,
    private val saveContactWorker: SaveContactWorker,
    private val eventEmitter: BleEventEmitter,
    private val currentTimestampProvider: () -> DateTime = { DateTime.now(DateTimeZone.UTC) },
    @Named(BluetoothModule.SCAN_INTERVAL_LENGTH)
    private val scanIntervalLength: Int,
    base64Decoder: (String) -> ByteArray = { Base64.decode(it, Base64.DEFAULT) },
    val base64Encoder: (ByteArray) -> String = { Base64.encodeToString(it, Base64.DEFAULT) },
    private val context: Context
) {

    private var knownDevices: MutableMap<String, BluetoothIdentifier> = mutableMapOf()
    private var devices: MutableList<ScanResult> = mutableListOf()

    private val sonarServiceUuidFilter = ScanFilter.Builder()
        .setServiceUuid(ParcelUuid(SONAR_SERVICE_UUID))
        .build()

    private var scanDisposable: Disposable? = null
    private var scanJob: Job? = null
    private val appleManufacturerId = 76
    private val encodedBackgroundIosServiceUuid: ByteArray =
        base64Decoder(BuildConfig.SONAR_ENCODED_BACKGROUND_IOS_SERVICE_UUID)

    /*
     When the iPhone app goes into the background iOS changes how services are advertised:
  
         1) The service uuid is now null
         2) The information to identify the service is encoded into the manufacturing data in a
         unspecified/undocumented way.
  
        The below filter is based on observation of the advertising packets produced by an iPhone running
        the app in the background.
       */
    private val sonarBackgroundedIPhoneFilter = ScanFilter.Builder()
        .setServiceUuid(null)
        .setManufacturerData(
            appleManufacturerId,
            encodedBackgroundIosServiceUuid
        )
        .build()

    private val settings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .build()

    fun start(coroutineScope: CoroutineScope) {
        val logOptions = LogOptions.Builder()
            .setLogLevel(LogConstants.DEBUG)
            .setLogger { level, tag, msg -> Timber.tag(tag).log(level, msg) }
            .build()
        RxBleClient.updateLogOptions(logOptions)

        scanJob = coroutineScope.launch {
            while (isActive) {

                displayLogOnScreen("scan - Starting")
                //Timber.d("scan - Starting")
                scanDisposable = scan()

                var attempts = 0
                while (attempts++ < 10 && devices.isEmpty()) {
                    if (!isActive) {
                        disposeScanDisposable()
                        return@launch
                    }
                    delay(scanIntervalLength.toLong() * 1_000)
                }

                displayLogOnScreen("scan - Stopping")
                //Timber.d("scan - Stopping")
                disposeScanDisposable()

                if (!isActive) return@launch

                // Some devices are unable to connect while a scan is running
                // or just after it finished
                delay(1_000)

                connectToEachDiscoveredDevice(coroutineScope)

                devices.clear()
            }
        }
    }

    fun stop() {
        disposeScanDisposable()
        scanJob?.cancel()
    }

    private fun connectToEachDiscoveredDevice(coroutineScope: CoroutineScope) {
        devices.distinctBy { it.bleDevice }.forEach { scanResult ->
            val macAddress = scanResult.bleDevice.macAddress
            val device = scanResult.bleDevice
            val identifier = knownDevices[macAddress]
            val txPowerAdvertised = scanResult.scanRecord.txPowerLevel

            val operation = if (identifier != null) {
                readOnlyRssi(identifier)
            } else {
                readIdAndRssi()
            }


            displayLogOnScreen("Connecting to $macAddress")
            //Timber.d("Connecting to $macAddress")
            connectAndPerformOperation(
                device,
                macAddress,
                txPowerAdvertised,
                coroutineScope,
                operation
            )
        }
    }

    private fun readIdAndRssi(): (RxBleConnection) -> Single<Pair<ByteArray, Int>> =
        { connection ->
            displayLogOnScreen( "readIdAndRssi negotiateMTU(connection)")
            negotiateMTU(connection)
                .flatMap {
                    displayLogOnScreen( "disableRetry(it)")
                    disableRetry(it)
                }
                .flatMap {
//                    displayLogOnScreen( "Single.zip()")
//                    displayLogOnScreen( "it.readCharacteristic(SONAR_IDENTITY_CHARACTERISTIC_UUID)"+it.readCharacteristic(SONAR_IDENTITY_CHARACTERISTIC_UUID))
//                    displayLogOnScreen( "it.readRssi()"+it.readRssi())



                    //it=Peripheral
//                    var rssi=it.readRssi()
//                    var characteristics=it.readCharacteristic(SONAR_IDENTITY_CHARACTERISTIC_UUID)

                    displayLogOnScreen( "reading characteristic and rssi")
                    Single.zip(
                        it.readCharacteristic(SONAR_IDENTITY_CHARACTERISTIC_UUID),
                        it.readRssi(),
                        BiFunction<ByteArray, Int, Pair<ByteArray, Int>> { characteristicValue, rssi ->
                            characteristicValue to rssi
                        }
                    )

                }
        }

    private fun readOnlyRssi(identifier: BluetoothIdentifier): (RxBleConnection) -> Single<Pair<ByteArray, Int>> =
        { connection: RxBleConnection ->
            connection.readRssi().map { rssi ->
                identifier.asBytes() to rssi
            }
        }

    private fun connectAndPerformOperation(
        device: RxBleDevice,
        macAddress: String,
        txPowerAdvertised: Int,
        coroutineScope: CoroutineScope,
        readOperation: (RxBleConnection) -> Single<Pair<ByteArray, Int>>
    ) {
        displayLogOnScreen("connectAndPerformOperation")
        val compositeDisposable = CompositeDisposable()
        device
            .establishConnection(false)
            .flatMapSingle {
                displayLogOnScreen("Connected to $macAddress")
                //Timber.d("Connected to $macAddress")
                readOperation(it)
            }
            .doOnSubscribe {
                displayLogOnScreen("doOnSubscribe")
                compositeDisposable.add(it)
            }
            .take(1)
            .blockingSubscribe(
                { (identifier, rssi) ->
                    onReadSuccess(
                        identifier,
                        rssi,
                        compositeDisposable,
                        macAddress,
                        txPowerAdvertised,
                        coroutineScope
                    )
                },
                { e ->
                    onReadError(e, compositeDisposable, macAddress)
                }
            )
    }

    private fun disposeScanDisposable() {
        scanDisposable?.dispose()
        scanDisposable = null
    }

    private fun scan(): Disposable? =
        rxBleClient
            .scanBleDevices(
                settings,
                sonarBackgroundedIPhoneFilter,
                sonarServiceUuidFilter
            )
            .subscribe(
                {
                    //displayLogOnScreen("Scan found = ${it.bleDevice}")
                    Timber.d("Scan found = ${it.bleDevice}")
                    devices.add(it)
                },
                ::scanError
            )

    private fun disableRetry(connection: RxBleConnection): Single<RxBleConnection> =
        connection.queue { bluetoothGatt, _, _ ->
            DisableRetryOnUnauthenticatedRead.bypassAuthenticationRetry(
                bluetoothGatt
            )
            Observable.just(connection)
        }.firstOrError()

    private fun onReadSuccess(
        identifierBytes: ByteArray,
        rssi: Int,
        connectionDisposable: CompositeDisposable,
        macAddress: String,
        txPowerAdvertised: Int,
        scope: CoroutineScope
    ) {

        displayLogOnScreen( "✅ onReadSuccess")
        connectionDisposable.dispose()
        if (identifierBytes.size != BluetoothIdentifier.SIZE) {
            throw IllegalArgumentException("Identifier has wrong size, must be ${BluetoothIdentifier.SIZE}, was ${identifierBytes.size}")
        }
        val identifier = BluetoothIdentifier.fromBytes(identifierBytes)
        updateKnownDevices(identifier, macAddress)
        displayLogOnScreen("seen MAC $macAddress as ${base64Encoder(identifier.cryptogram.asBytes())
            .drop(2)
            .take(12)}")
//        Timber.d(
//            "seen MAC $macAddress as ${base64Encoder(identifier.cryptogram.asBytes())
//                .drop(2)
//                .take(12)}"
//        )
        storeEvent(identifier, rssi, scope, txPowerAdvertised)
    }

    private fun onReadError(
        e: Throwable,
        connectionDisposable: CompositeDisposable,
        macAddress: String
    ) {

        connectionDisposable.dispose()
        displayLogOnScreen("❌ failed reading from $macAddress - $e")
        //Timber.e("failed reading from $macAddress - $e")
        eventEmitter.errorEvent(macAddress, e)
    }

    private fun updateKnownDevices(identifier: BluetoothIdentifier, macAddress: String) {
        val previousMac = knownDevices.entries.firstOrNull { (_, v) ->
            v.cryptogram.asBytes().contentEquals(identifier.cryptogram.asBytes())
        }
        knownDevices.remove(previousMac?.key)
        knownDevices[macAddress] = identifier
        displayLogOnScreen("Previous MAC was ${previousMac?.key}, new is $macAddress")
        //Timber.d("Previous MAC was ${previousMac?.key}, new is $macAddress")
    }

    private fun negotiateMTU(connection: RxBleConnection): Single<RxBleConnection> {
        // the overhead appears to be 2 bytes
        return connection.requestMtu(2 + BluetoothIdentifier.SIZE)
            .doOnSubscribe { Timber.d("Negotiating MTU started") }
            .doOnError { e: Throwable? ->
                displayLogOnScreen("Failed to negotiate MTU: $e")
                //Timber.e("Failed to negotiate MTU: $e")
                Observable.error<Throwable?>(e)
            }
            .doOnSuccess {
                displayLogOnScreen("Negotiated MTU: $it")
                //Timber.d("Negotiated MTU: $it")
            }
            .ignoreElement()
            .andThen(Single.just(connection))
    }

    private fun scanError(e: Throwable) {
        displayLogOnScreen("❌ Scan failed with: $e")
        //Timber.e("Scan failed with: $e")
    }

    private fun storeEvent(
        identifier: BluetoothIdentifier,
        rssi: Int,
        scope: CoroutineScope,
        txPowerAdvertised: Int
    ) {

        displayLogOnScreen("✅ storeEvent")

        eventEmitter.successfulContactEvent(
            identifier,
            listOf(rssi),
            txPowerAdvertised
        )

        saveContactWorker.createOrUpdateContactEvent(
            scope,
            identifier,
            rssi,
            currentTimestampProvider(),
            txPowerAdvertised
        )
    }

    private fun displayLogOnScreen(log: String){
        Timber.d("displayLogOnScreen:"+log)
        val intent = Intent()
        intent.action = "BROADCAST_DISPLAY_LOG"
        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        intent.putExtra("log",log)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}
