/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.notifications

import effiia.com.Safe2.sonar.android.app.status.UserStateMachine
import javax.inject.Inject

class ExposureMessageHandler @Inject constructor(
    private val userStateMachine: UserStateMachine,
    private val exposedNotification: ExposedNotification
) {
    fun handle(message: ExposureMessage) {
        userStateMachine.transitionOnExposure(
            exposureDate = message.date,
            onStateChanged = { exposedNotification.show() }
        )
    }
}
