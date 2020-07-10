/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.diagnose

import org.joda.time.DateTime
import org.junit.Test
import effiia.com.Safe2.sonar.android.app.status.Symptom.COUGH
import effiia.com.Safe2.sonar.android.app.status.Symptom.TEMPERATURE
import effiia.com.Safe2.sonar.android.app.testhelpers.base.EspressoTest
import effiia.com.Safe2.sonar.android.app.testhelpers.robots.DiagnoseSubmitRobot

class DiagnoseSubmitActivityTest : EspressoTest() {

    private val diagnoseSubmitRobot = DiagnoseSubmitRobot()

    @Test
    fun confirmationIsRequired() {
        startTestActivity<DiagnoseSubmitActivity> {
            putSymptoms(setOf(COUGH, TEMPERATURE))
            putExtra("SYMPTOMS_DATE", DateTime.now().millis)
        }

        diagnoseSubmitRobot.submit()
        diagnoseSubmitRobot.checkConfirmationIsNeeded()
    }
}
