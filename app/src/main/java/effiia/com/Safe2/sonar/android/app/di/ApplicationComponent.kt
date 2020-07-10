/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.di

import com.polidea.rxandroidble2.RxBleClient
import dagger.Component
import effiia.com.Safe2.sonar.android.app.FlowTestStartActivity
import effiia.com.Safe2.sonar.android.app.MainActivity
import effiia.com.Safe2.sonar.android.app.ble.BluetoothService
import effiia.com.Safe2.sonar.android.app.common.BaseActivity
import effiia.com.Safe2.sonar.android.app.contactevents.DeleteOutdatedEventsWork
import effiia.com.Safe2.sonar.android.app.debug.TesterActivity
import effiia.com.Safe2.sonar.android.app.di.module.AppModule
import effiia.com.Safe2.sonar.android.app.di.module.BluetoothModule
import effiia.com.Safe2.sonar.android.app.di.module.CryptoModule
import effiia.com.Safe2.sonar.android.app.di.module.NetworkModule
import effiia.com.Safe2.sonar.android.app.di.module.NotificationsModule
import effiia.com.Safe2.sonar.android.app.di.module.PersistenceModule
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseAnosmiaActivity
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseCloseActivity
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseCoughActivity
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseSneezeActivity
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseStomachActivity
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseSubmitActivity
import effiia.com.Safe2.sonar.android.app.diagnose.DiagnoseTemperatureActivity
import effiia.com.Safe2.sonar.android.app.diagnose.SubmitContactEventsWork
import effiia.com.Safe2.sonar.android.app.diagnose.review.DiagnoseReviewActivity
import effiia.com.Safe2.sonar.android.app.interstitials.ApplyForTestActivity
import effiia.com.Safe2.sonar.android.app.interstitials.CurrentAdviceActivity
import effiia.com.Safe2.sonar.android.app.notifications.NotificationChannels
import effiia.com.Safe2.sonar.android.app.notifications.NotificationService
import effiia.com.Safe2.sonar.android.app.notifications.reminders.ReminderBroadcastReceiver
import effiia.com.Safe2.sonar.android.app.notifications.TokenRefreshWork
import effiia.com.Safe2.sonar.android.app.onboarding.EnableLocationActivity
import effiia.com.Safe2.sonar.android.app.onboarding.GrantLocationPermissionActivity
import effiia.com.Safe2.sonar.android.app.onboarding.PermissionActivity
import effiia.com.Safe2.sonar.android.app.onboarding.PostCodeActivity
import effiia.com.Safe2.sonar.android.app.receivers.BootCompletedReceiver
import effiia.com.Safe2.sonar.android.app.receivers.PackageReplacedReceiver
import effiia.com.Safe2.sonar.android.app.referencecode.ReferenceCodeActivity
import effiia.com.Safe2.sonar.android.app.registration.RegistrationWork
import effiia.com.Safe2.sonar.android.app.status.StatusActivity
import effiia.com.Safe2.sonar.android.app.util.LocationHelper
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        PersistenceModule::class,
        BluetoothModule::class,
        CryptoModule::class,
        NetworkModule::class,
        NotificationsModule::class
    ]
)
interface ApplicationComponent {
    fun inject(activity: BaseActivity)
    fun inject(activity: PermissionActivity)
    fun inject(activity: EnableLocationActivity)
    fun inject(activity: GrantLocationPermissionActivity)
    fun inject(activity: StatusActivity)
    fun inject(activity: DiagnoseReviewActivity)
    fun inject(activity: DiagnoseCloseActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: FlowTestStartActivity)
    fun inject(activity: PostCodeActivity)
    fun inject(activity: TesterActivity)
    fun inject(activity: DiagnoseSubmitActivity)
    fun inject(activity: DiagnoseSneezeActivity)
    fun inject(activity: DiagnoseStomachActivity)
    fun inject(activity: DiagnoseAnosmiaActivity)
    fun inject(activity: DiagnoseCoughActivity)
    fun inject(activity: DiagnoseTemperatureActivity)
    fun inject(activity: ReferenceCodeActivity)
    fun inject(activity: ApplyForTestActivity)
    fun inject(activity: CurrentAdviceActivity)

    fun inject(service: BluetoothService)
    fun inject(service: NotificationService)


    fun inject(receiver: PackageReplacedReceiver)
    fun inject(receiver: BootCompletedReceiver)
    fun inject(receiver: ReminderBroadcastReceiver)

    fun deleteOutdatedEventsWork(): DeleteOutdatedEventsWork
    fun registrationWork(): RegistrationWork
    fun submitContactEventsWork(): SubmitContactEventsWork
    fun tokenRefreshWork(): TokenRefreshWork

    fun rxBleClient(): RxBleClient
    fun locationHelper(): LocationHelper
    fun notificationChannels(): NotificationChannels
}
