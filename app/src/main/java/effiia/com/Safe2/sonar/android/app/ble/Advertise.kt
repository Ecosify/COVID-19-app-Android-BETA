/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.ble

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Advertise @Inject constructor(private val bluetoothLeAdvertiser: BluetoothLeAdvertiser) {
    private val advertiseData: AdvertiseData =
        AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SONAR_SERVICE_UUID))
            .setIncludeDeviceName(false)
            .addManufacturerData(GOOGLE_BLUETOOTH_MANUFACTURE_ID, byteArrayOf())
            .setIncludeTxPowerLevel(true)
            .build()

    private val advertiseSettings: AdvertiseSettings =
        AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()

    private val advertisingCallback = AdvertisingCallback()

    fun start() {
        Timber.d("BluetoothLeAdvertiser startAdvertising")
        bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertisingCallback)
    }

    fun stop() {
        Timber.d("BluetoothLeAdvertiser stopAdvertising")
        bluetoothLeAdvertiser.stopAdvertising(advertisingCallback)
    }
}

private class AdvertisingCallback : AdvertiseCallback() {
    override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
        Timber.d("BluetoothLeAdvertiser Started advertising with settings $settingsInEffect")
    }

    override fun onStartFailure(errorCode: Int) {
        Timber.e("BluetoothLeAdvertiser Failed to start with error code $errorCode")
    }
}