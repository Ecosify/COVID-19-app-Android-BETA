/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.testhelpers

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.polidea.rxandroidble2.RxBleClient
import dagger.Component
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import effiia.com.Safe2.sonar.android.app.ble.DebugBleEventTracker
import effiia.com.Safe2.sonar.android.app.ble.NoOpBleEventEmitter
import effiia.com.Safe2.sonar.android.app.ble.SaveContactWorker
import effiia.com.Safe2.sonar.android.app.ble.Scanner
import effiia.com.Safe2.sonar.android.app.di.ApplicationComponent
import effiia.com.Safe2.sonar.android.app.di.module.AppModule
import effiia.com.Safe2.sonar.android.app.di.module.BluetoothModule
import effiia.com.Safe2.sonar.android.app.di.module.CryptoModule
import effiia.com.Safe2.sonar.android.app.di.module.NetworkModule
import effiia.com.Safe2.sonar.android.app.di.module.PersistenceModule
import effiia.com.Safe2.sonar.android.app.http.KeyStorage
import effiia.com.Safe2.sonar.android.app.inbox.UserInbox
import effiia.com.Safe2.sonar.android.app.onboarding.OnboardingStatusProvider
import effiia.com.Safe2.sonar.android.app.onboarding.PostCodeProvider
import effiia.com.Safe2.sonar.android.app.registration.ActivationCodeProvider
import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import effiia.com.Safe2.sonar.android.app.registration.TokenRetriever
import effiia.com.Safe2.sonar.android.app.status.UserStateStorage
import effiia.com.Safe2.sonar.android.app.storage.AppDatabase
import effiia.com.Safe2.sonar.android.app.util.DeviceDetection
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        PersistenceModule::class,
        BluetoothModule::class,
        CryptoModule::class,
        NetworkModule::class,
        TestNotificationsModule::class
    ]
)
interface TestAppComponent : ApplicationComponent {
    fun getSonarIdProvider(): SonarIdProvider
    fun getKeyStorage(): KeyStorage
    fun getAppDatabase(): AppDatabase
    fun getUserStateStorage(): UserStateStorage
    fun getUserInbox(): UserInbox
    fun getOnboardingStatusProvider(): OnboardingStatusProvider
    fun getActivationCodeProvider(): ActivationCodeProvider
    fun getPostCodeProvider(): PostCodeProvider
}

class TestBluetoothModule(
    private val appContext: Context,
    private val rxBleClient: RxBleClient,
    private val currentTimestampProvider: () -> DateTime,
    private val scanIntervalLength: Int = 2
) : BluetoothModule(appContext, scanIntervalLength) {

    override fun provideRxBleClient(): RxBleClient =
        rxBleClient

    override fun provideScanner(
        rxBleClient: RxBleClient,
        saveContactWorker: SaveContactWorker,
        debugBleEventEmitter: DebugBleEventTracker,
        noOpBleEventEmitter: NoOpBleEventEmitter
    ): Scanner =
        Scanner(
            rxBleClient,
            saveContactWorker,
            debugBleEventEmitter,
            currentTimestampProvider,
            scanIntervalLength
        )

    var simulateUnsupportedDevice = false

    var simulateTablet = false

    override fun provideDeviceDetection(): DeviceDetection =
        if (simulateUnsupportedDevice || simulateTablet) {
            val adapter =
                if (simulateUnsupportedDevice) null else BluetoothAdapter.getDefaultAdapter()
            DeviceDetection(adapter, appContext, simulateTablet)
        } else {
            super.provideDeviceDetection()
        }

    fun reset() {
        simulateUnsupportedDevice = false
        simulateTablet = false
    }
}

@Module
class TestNotificationsModule {

    @Provides
    fun provideTokenRetriever(): TokenRetriever =
        object : TokenRetriever {
            override suspend fun retrieveToken() = "test firebase token #010"
        }
}
