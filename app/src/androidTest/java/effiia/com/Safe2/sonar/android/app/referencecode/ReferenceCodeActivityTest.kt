/*
 * Copyright © 2020 NHSX. All rights reserved.
 */
package effiia.com.Safe2.sonar.android.app.referencecode

import org.junit.Test
import effiia.com.Safe2.sonar.android.app.testhelpers.base.EspressoTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.ReferenceCodeRobot

class ReferenceCodeActivityTest : EspressoTest() {

    private val referenceCodeRobot = ReferenceCodeRobot()

    @Test
    fun showsReferenceCode() {
        startActivityWithState<ReferenceCodeActivity>()
        referenceCodeRobot.checkReferenceCodeIs("REF CODE #202")
    }

    @Test
    fun showsTestResultMeaning() {
        val intent = ReferenceCodeActivity.intentWithFocusKey(testAppContext.app)

        startTestActivity(intent)

        referenceCodeRobot.checkTestResultMeaningSection()
    }
}
