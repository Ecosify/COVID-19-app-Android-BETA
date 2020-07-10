/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import effiia.com.Safe2.sonar.android.app.ble.BluetoothService
import effiia.com.Safe2.sonar.android.app.common.ColorInversionAwareActivity
import effiia.com.Safe2.sonar.android.app.edgecases.DeviceNotSupportedActivity
import effiia.com.Safe2.sonar.android.app.edgecases.TabletNotSupportedActivity
import effiia.com.Safe2.sonar.android.app.onboarding.MainOnboardingActivity
import effiia.com.Safe2.sonar.android.app.onboarding.OnboardingStatusProvider
import effiia.com.Safe2.sonar.android.app.onboarding.PermissionActivity
import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import effiia.com.Safe2.sonar.android.app.status.startStatusActivity
import effiia.com.Safe2.sonar.android.app.util.DeviceDetection
import javax.inject.Inject

class MainActivity : ColorInversionAwareActivity() {

    @Inject
    lateinit var sonarIdProvider: SonarIdProvider

    @Inject
    lateinit var onboardingStatusProvider: OnboardingStatusProvider

    @Inject
    lateinit var deviceDetection: DeviceDetection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        when {
            deviceDetection.isTablet() -> {
                finish()
                TabletNotSupportedActivity.start(this)
            }
            deviceDetection.isUnsupported() -> {
                finish()
                DeviceNotSupportedActivity.start(this)
            }
            sonarIdProvider.hasProperSonarId() -> {
                BluetoothService.start(this)
                startStatusActivity()
            }
            onboardingStatusProvider.get() -> {
                startStatusActivity()
            }
            else -> {
                finish()
                PermissionActivity.start(this)
            }
        }
    }

    companion object {
        fun start(context: Context) =
            context.startActivity(getIntent(context))

        fun getIntent(context: Context) =
            Intent(context, MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
    }
}
