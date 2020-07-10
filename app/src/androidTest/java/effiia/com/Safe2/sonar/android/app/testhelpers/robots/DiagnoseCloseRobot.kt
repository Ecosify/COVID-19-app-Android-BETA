/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.testhelpers.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import effiia.com.Safe2.sonar.android.app.R

class DiagnoseCloseRobot {

    fun checkActivityIsDisplayed() {
        onView(withId(R.id.close_review_btn)).check(matches(isDisplayed()))
    }

    fun close() {
        onView(withId(R.id.close_review_btn)).perform(click())
    }
}
