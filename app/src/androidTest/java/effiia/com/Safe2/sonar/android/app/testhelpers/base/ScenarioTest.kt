/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.testhelpers.base

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import effiia.com.Safe2.sonar.android.app.FlowTestStartActivity
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.status.UserState

abstract class ScenarioTest : EspressoTest() {

    @get:Rule
    val activityRule: ActivityTestRule<FlowTestStartActivity> =
        ActivityTestRule(FlowTestStartActivity::class.java)

    @Before
    fun setupFlowTestActivity() {
        startTestActivity<FlowTestStartActivity>()
    }

    protected fun startAppWith(state: UserState) {
        testAppContext.setFullValidUser(state)
        launchFlowTestStartActivity()
    }

    protected fun startAppWithEmptyState() {
        launchFlowTestStartActivity()
    }

    private fun launchFlowTestStartActivity() {
        onView(withId(R.id.start_main_activity)).perform(click())
    }
}
