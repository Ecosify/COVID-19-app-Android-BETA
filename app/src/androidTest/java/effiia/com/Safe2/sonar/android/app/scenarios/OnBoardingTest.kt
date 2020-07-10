/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.scenarios

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.status.DefaultState
import effiia.com.Safe2.sonar.android.app.testhelpers.base.ScenarioTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.MainOnboardingRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.PermissionRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.PostCodeRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.StatusRobot

class OnBoardingTest : ScenarioTest() {

    private val mainOnBoardingRobot = MainOnboardingRobot()
    private val postCodeRobot = PostCodeRobot()
    private val permissionRobot = PermissionRobot()
    private val statusRobot = StatusRobot()

    @Test
    fun registration() {
        testAppContext.simulateBackendDelay(400)

        startAppWithEmptyState()
        mainOnBoardingRobot.clickConfirmOnboarding()

        postCodeRobot.checkActivityIsDisplayed()
        postCodeRobot.enterPostCode("E1")
        postCodeRobot.clickContinue()

        permissionRobot.checkActivityIsDisplayed()
        permissionRobot.clickContinue()

        statusRobot.checkActivityIsDisplayed(DefaultState::class)
        statusRobot.checkFinalisingSetup()

        testAppContext.verifyRegistrationFlow()

        statusRobot.waitForRegistrationToComplete()
        statusRobot.checkFeelUnwellIsDisplayed()
    }
}
