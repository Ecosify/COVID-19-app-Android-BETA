/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.edgecases

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.EspressoTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.DeviceNotSupportedRobot

class DeviceNotSupportedActivityTest : EspressoTest() {

    private val robot = DeviceNotSupportedRobot()

    @Test
    fun displaysExpectedViews() {
        startTestActivity<DeviceNotSupportedActivity>()

        robot.checkScreenIsDisplayed()
    }
}
