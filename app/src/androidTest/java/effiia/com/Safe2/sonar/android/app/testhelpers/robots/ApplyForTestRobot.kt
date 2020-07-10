/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.testhelpers.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import effiia.com.Safe2.sonar.android.app.R

class ApplyForTestRobot {

    fun checkActivityIsDisplayed() {
        onView(withId(R.id.apply_for_test_title)).check(matches(isDisplayed()))
    }
}
