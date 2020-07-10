/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.di.module

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import com.polidea.rxandroidble2.RxBleClient
import dagger.Module
import dagger.Provides
import effiia.com.Safe2.sonar.android.app.BuildConfig
import effiia.com.Safe2.sonar.android.app.ble.DebugBleEventTracker
import effiia.com.Safe2.sonar.android.app.ble.NoOpBleEventEmitter
import effiia.com.Safe2.sonar.android.app.ble.SaveContactWorker
import effiia.com.Safe2.sonar.android.app.ble.Scanner
import effiia.com.Safe2.sonar.android.app.util.DeviceDetection
import javax.inject.Named

@Module
open class BluetoothModule(
    private val applicationContext: Context,
    private val scanIntervalLength: Int
) {
    @Provides
    fun provideBluetoothManager(): BluetoothManager =
        getSystemService(applicationContext, BluetoothManager::class.java)!!

    @Provides
    fun provideBluetoothAdvertiser(bluetoothManager: BluetoothManager): BluetoothLeAdvertiser =
        bluetoothManager.adapter.bluetoothLeAdvertiser

    @Provides
    open fun provideRxBleClient(): RxBleClient =
        RxBleClient.create(applicationContext)

    @Provides
    open fun provideDeviceDetection(): DeviceDetection =
        DeviceDetection(BluetoothAdapter.getDefaultAdapter(), applicationContext)

    @Provides
    open fun provideScanner(
        rxBleClient: RxBleClient,
        saveContactWorker: SaveContactWorker,
        debugBleEventEmitter: DebugBleEventTracker,
        noOpBleEventEmitter: NoOpBleEventEmitter
    ): Scanner {
        val eventEmitter = when (BuildConfig.BUILD_TYPE) {
            "debug", "internal" -> debugBleEventEmitter
            else -> noOpBleEventEmitter
        }
        return Scanner(
            rxBleClient,
            saveContactWorker,
            eventEmitter,
            scanIntervalLength = scanIntervalLength,
            context=applicationContext
        )
    }

    @Provides
    @Named(SCAN_INTERVAL_LENGTH)
    fun provideScanIntervalLength() = scanIntervalLength

    companion object {
        const val SCAN_INTERVAL_LENGTH = "SCAN_INTERVAL_LENGTH"
    }
}
