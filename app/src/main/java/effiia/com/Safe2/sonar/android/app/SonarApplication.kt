/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app

import android.app.Application
import android.content.Context
import androidx.work.ListenableWorker
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.polidea.rxandroidble2.exceptions.BleException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import net.danlew.android.joda.JodaTimeAndroid
import org.bouncycastle.jce.provider.BouncyCastleProvider
import timber.log.Timber
import effiia.com.Safe2.sonar.android.app.BuildConfig.BASE_URL
import effiia.com.Safe2.sonar.android.app.BuildConfig.SONAR_HEADER_VALUE
import effiia.com.Safe2.sonar.android.app.contactevents.DeleteOutdatedEventsWorker
import effiia.com.Safe2.sonar.android.app.crypto.PROVIDER_NAME
import effiia.com.Safe2.sonar.android.app.di.ApplicationComponent
import effiia.com.Safe2.sonar.android.app.di.DaggerApplicationComponent
import effiia.com.Safe2.sonar.android.app.di.module.AppModule
import effiia.com.Safe2.sonar.android.app.di.module.BluetoothModule
import effiia.com.Safe2.sonar.android.app.di.module.CryptoModule
import effiia.com.Safe2.sonar.android.app.di.module.NetworkModule
import effiia.com.Safe2.sonar.android.app.di.module.NotificationsModule
import effiia.com.Safe2.sonar.android.app.di.module.PersistenceModule
import effiia.com.Safe2.sonar.android.app.util.AndroidLocationHelper
import effiia.com.Safe2.sonar.android.app.util.AndroidNotificationManagerHelper
import effiia.com.Safe2.sonar.android.app.util.FileLogTree
import effiia.com.Safe2.sonar.android.app.util.appVersion
import java.security.KeyStore
import java.security.Security

class SonarApplication : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = buildApplicationComponent()
        appComponent.notificationChannels().createChannels()

        configureBouncyCastleProvider()
        configureRxJavaErrorHandler()
        JodaTimeAndroid.init(this)
        FirebaseApp.initializeApp(this)

        when (BuildConfig.BUILD_TYPE) {
            "internal", "debug" -> {
                Timber.plant(Timber.DebugTree())
                Timber.plant(FileLogTree(this))
                logFirebaseToken()
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            }
        }

        DeleteOutdatedEventsWorker.schedule(this)
    }

    private fun logFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                try {
                    if (!task.isSuccessful) {
                        Timber.d(task.exception, "FirebaseInstanceId.getInstanceId failed")
                        return@addOnCompleteListener
                    }
                    val token = task.result?.token
                    Timber.d(task.exception, "Firebase Token = $token")
                } catch (exception: Exception) {
                    Timber.e(task.exception, "Firebase Token retrieval failed")
                }
            }
    }

    private fun configureRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException && throwable.cause is BleException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable)
        }
    }

    private fun buildApplicationComponent(): ApplicationComponent =
        DaggerApplicationComponent.builder()
            .appModule(
                AppModule(
                    this,
                    AndroidLocationHelper(this),
                    AndroidNotificationManagerHelper(this)
                )
            )
            .persistenceModule(PersistenceModule(this))
            .bluetoothModule(BluetoothModule(this, scanIntervalLength = 8))
            .cryptoModule(
                CryptoModule(
                    this,
                    KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                )
            )
            .networkModule(
                NetworkModule(
                    appVersion = appVersion(),
                    baseUrl = BASE_URL,
                    sonarHeaderValue = SONAR_HEADER_VALUE
                )
            )
            .notificationsModule(NotificationsModule())
            .build()

    private fun configureBouncyCastleProvider() {
        // Remove existing built in Bouncy Castle
        Security.removeProvider(PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}

val ListenableWorker.appComponent: ApplicationComponent
    get() = (applicationContext as SonarApplication).appComponent

val Context.appComponent: ApplicationComponent
    get() = (applicationContext as SonarApplication).appComponent
