/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.onboarding

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.EspressoTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.ExplanationRobot
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.MainOnboardingRobot

class MainOnboardingActivityTest : EspressoTest() {

    private val mainOnBoardingRobot = MainOnboardingRobot()
    private val explanationRobot = ExplanationRobot()

    @Test
    fun explanation() {
        startTestActivity<MainOnboardingActivity>()

        mainOnBoardingRobot.checkActivityIsDisplayed()
        mainOnBoardingRobot.clickExplanationLink()

        explanationRobot.checkActivityIsDisplayed()
        explanationRobot.clickBackButton()

        mainOnBoardingRobot.checkActivityIsDisplayed()
    }
}
