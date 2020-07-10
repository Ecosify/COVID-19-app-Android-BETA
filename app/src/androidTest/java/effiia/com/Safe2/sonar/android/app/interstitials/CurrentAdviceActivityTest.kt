/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.interstitials

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.EspressoTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.CurrentAdviceRobot

class CurrentAdviceActivityTest : EspressoTest() {

    private val currentAdviceRobot = CurrentAdviceRobot()

    @Test
    fun showsAdviceSpecificForStateWithoutUntilDate() {
        startActivityWithState<CurrentAdviceActivity>()

        currentAdviceRobot.checkIconIsShowing()
        currentAdviceRobot.checkActivityIsDisplayed()
        currentAdviceRobot.checkCorrectStateIsDisplay(testData.defaultState)
        currentAdviceRobot.checkAdviceUrlIsDisplayed()
    }

    @Test
    fun showsAdviceSpecificForStateWithUntilDate() {
        startActivityWithState<CurrentAdviceActivity>(testData.symptomaticState)

        currentAdviceRobot.checkIconIsShowing()
        currentAdviceRobot.checkActivityIsDisplayed()
        currentAdviceRobot.checkCorrectStateIsDisplay(testData.symptomaticState)
        currentAdviceRobot.checkAdviceUrlIsDisplayed()
    }
}
