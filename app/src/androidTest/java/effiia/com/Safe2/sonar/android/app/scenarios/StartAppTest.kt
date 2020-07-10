/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.scenarios

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.status.DefaultState
import effiia.com.Safe2.sonar.android.app.testhelpers.base.ScenarioTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.DeviceNotSupportedRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.StatusRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.TabletNotSupportedRobot

class StartAppTest : ScenarioTest() {

    private val statusRobot = StatusRobot()

    @Test
    fun unsupportedDevice() {
        testAppContext.simulateUnsupportedDevice()

        startAppWith(testData.defaultState)

        val robot = DeviceNotSupportedRobot()
        robot.checkScreenIsDisplayed()
    }

    @Test
    fun tabletNotSupported() {
        testAppContext.simulateTablet()

        startAppWith(testData.defaultState)

        val robot = TabletNotSupportedRobot()
        robot.checkScreenIsDisplayed()
    }

    @Test
    fun launchWhenOnBoardingIsFinishedButNotRegistered() {
        testAppContext.setFinishedOnboarding()

        startAppWith(testData.defaultState)

        statusRobot.checkActivityIsDisplayed(DefaultState::class)
    }
}
