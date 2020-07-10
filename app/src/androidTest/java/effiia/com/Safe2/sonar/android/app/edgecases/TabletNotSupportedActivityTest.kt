/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.edgecases

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.EspressoTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.TabletNotSupportedRobot

class TabletNotSupportedActivityTest : EspressoTest() {

    private val robot = TabletNotSupportedRobot()

    @Test
    fun displaysExpectedViews() {
        startTestActivity<TabletNotSupportedActivity>()

        robot.checkScreenIsDisplayed()
    }
}
