/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import effiia.com.Safe2.sonar.android.app.appComponent
import effiia.com.Safe2.sonar.android.app.ble.BluetoothService
import effiia.com.Safe2.sonar.android.app.notifications.reminders.ReminderScheduler
import effiia.com.Safe2.sonar.android.app.registration.RegistrationManager
import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import javax.inject.Inject

class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var sonarIdProvider: SonarIdProvider

    @Inject
    lateinit var registrationManager: RegistrationManager

    override fun onReceive(context: Context, intent: Intent) {
        context.appComponent.inject(this)

        if (intent.action != Intent.ACTION_BOOT_COMPLETED)
            return

        startBluetoothService(context)
        setReminder()
    }

    private fun setReminder() {
        reminderScheduler.reschedulePendingReminder()
    }

    private fun startBluetoothService(context: Context) {
        if (sonarIdProvider.hasProperSonarId())
            BluetoothService.start(context)
    }
}
