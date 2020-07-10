/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import effiia.com.Safe2.sonar.android.app.crypto.BluetoothIdProvider
import javax.inject.Inject

class GattServer @Inject constructor(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val bluetoothIdProvider: BluetoothIdProvider
) {
    private val keepAliveCharacteristic = BluetoothGattCharacteristic(
        SONAR_KEEPALIVE_CHARACTERISTIC_UUID,
        PROPERTY_READ + PROPERTY_WRITE + PROPERTY_WRITE_NO_RESPONSE + PROPERTY_NOTIFY,
        PERMISSION_READ + PERMISSION_WRITE
    ).also {
        it.addDescriptor(
            BluetoothGattDescriptor(
                NOTIFY_DESCRIPTOR_UUID,
                PERMISSION_READ + PERMISSION_WRITE
            )
        )
    }

    private val identityCharacteristic = BluetoothGattCharacteristic(
        SONAR_IDENTITY_CHARACTERISTIC_UUID,
        PROPERTY_READ + PROPERTY_WRITE + PROPERTY_WRITE_NO_RESPONSE + PROPERTY_NOTIFY,
        PERMISSION_READ + PERMISSION_WRITE
    ).also {
        it.addDescriptor(
            BluetoothGattDescriptor(
                NOTIFY_DESCRIPTOR_UUID,
                PERMISSION_READ + PERMISSION_WRITE
            )
        )
    }

    private val service: BluetoothGattService =
        BluetoothGattService(SONAR_SERVICE_UUID, SERVICE_TYPE_PRIMARY)
            .also {
                it.addCharacteristic(
                    identityCharacteristic
                )
                it.addCharacteristic(
                    keepAliveCharacteristic
                )
            }

    private var server: BluetoothGattServer? = null
    private var gattWrapper: GattWrapper? = null

    fun start(coroutineScope: CoroutineScope) {
        displayLogOnScreen( "Bluetooth Gatt start")
        //Timber.d("Bluetooth Gatt start")
        val callback = object : BluetoothGattServerCallback() {
            override fun onCharacteristicReadRequest(
                device: BluetoothDevice,
                requestId: Int,
                offset: Int,
                characteristic: BluetoothGattCharacteristic
            ) {
                var descriptors=characteristic.getDescriptors()
                //displayLogOnScreen( "💜 didDiscover peripheral "+descriptors[0].uuid)
                gattWrapper?.respondToCharacteristicRead(device, requestId, characteristic)
            }

            override fun onConnectionStateChange(
                device: BluetoothDevice?,
                status: Int,
                newState: Int
            ) {
                super.onConnectionStateChange(device, status, newState)
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gattWrapper?.deviceDisconnected(device)
                }
            }

            override fun onDescriptorWriteRequest(
                device: BluetoothDevice?,
                requestId: Int,
                descriptor: BluetoothGattDescriptor?,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray?
            ) {
                gattWrapper?.respondToDescriptorWrite(device, descriptor, responseNeeded, requestId)
            }
        }

        server = bluetoothManager.openGattServer(context, callback)
        server?.addService(service)

        gattWrapper = GattWrapper(
            server,
            coroutineScope,
            bluetoothManager,
            bluetoothIdProvider,
            keepAliveCharacteristic
        )
    }

    fun stop() {
        displayLogOnScreen( "Bluetooth Gatt start")
        //Timber.d("Bluetooth Gatt stop")
        server?.close()
        server = null
        gattWrapper?.notifyJob?.cancel()
        gattWrapper = null
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
