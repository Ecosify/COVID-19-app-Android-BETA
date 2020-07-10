/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.testhelpers.robots

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import effiia.com.Safe2.sonar.android.app.R
import effiia.com.Safe2.sonar.android.app.testhelpers.checkViewHasText

class EdgeCaseRobot {

    fun clickTakeAction() {
        onView(withId(R.id.takeActionButton)).perform(click())
    }

    fun checkTitle(@StringRes titleId: Int) {
        checkViewHasText(R.id.edgeCaseTitle, titleId)
    }
}
