/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.registration

import java.util.concurrent.TimeUnit

data class ActivationCodeWaitTime(
    val timeDelay: Long,
    val timeUnit: TimeUnit
)
