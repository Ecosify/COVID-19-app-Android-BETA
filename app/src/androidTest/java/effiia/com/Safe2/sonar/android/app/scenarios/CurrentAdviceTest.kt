/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.scenarios

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.ScenarioTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.CurrentAdviceRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.StatusRobot

class CurrentAdviceTest : ScenarioTest() {

    private val statusRobot = StatusRobot()
    private val currentAdviceRobot = CurrentAdviceRobot()

    @Test
    fun navigatesIntoCurrentAdviceBySelectingCard() {
        startAppWith(testData.symptomaticState)

        statusRobot.clickCurrentAdviceCard()

        currentAdviceRobot.checkActivityIsDisplayed()
    }
}
