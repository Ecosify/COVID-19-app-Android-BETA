/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.notifications

import effiia.com.Safe2.sonar.android.app.inbox.TestInfo
import effiia.com.Safe2.sonar.android.app.status.UserStateMachine
import javax.inject.Inject

class TestResultMessageHandler @Inject constructor(
    private val userStateMachine: UserStateMachine,
    private val testResultNotification: TestResultNotification
) {

    fun handle(message: TestResultMessage) {

        val testInfo = TestInfo(message.result, message.date)

        userStateMachine.transitionOnTestResult(testInfo)
        testResultNotification.show()
    }
}
