/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.scenarios

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.ScenarioTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.ApplyForTestRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.StatusRobot

class BookTestForCoronavirusTest : ScenarioTest() {

    private val statusRobot = StatusRobot()
    private val applyForTestRobot = ApplyForTestRobot()

    @Test
    fun clickOrderTestCardShowsApplyForTest() {
        startAppWith(testData.symptomaticState)

        statusRobot.clickBookTestCard()

        applyForTestRobot.checkActivityIsDisplayed()
    }
}
