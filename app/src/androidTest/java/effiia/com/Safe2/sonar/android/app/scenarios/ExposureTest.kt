/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.scenarios

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.status.DefaultState
import effiia.com.Safe2.sonar.android.app.status.ExposedState
import effiia.com.Safe2.sonar.android.app.status.SymptomaticState
import effiia.com.Safe2.sonar.android.app.testhelpers.base.ScenarioTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.StatusRobot

class ExposureTest : ScenarioTest() {

    private val statusRobot = StatusRobot()

    @Test
    fun whileInNeutral() {
        startAppWith(testData.defaultState)

        statusRobot.checkActivityIsDisplayed(DefaultState::class)

        testAppContext.apply {
            simulateExposureNotificationReceived()
            clickOnNotification(
                R.string.contact_alert_notification_title,
                R.string.contact_alert_notification_text
            )
        }

        statusRobot.checkActivityIsDisplayed(ExposedState::class)
        statusRobot.checkStatusDescription(userState())
    }

    @Test
    fun whileInSymptomatic() {
        startAppWith(testData.symptomaticYesterday())

        statusRobot.checkActivityIsDisplayed(SymptomaticState::class)

        testAppContext.apply {
            simulateExposureNotificationReceived()
        }

        statusRobot.checkActivityIsDisplayed(SymptomaticState::class)
    }
}
