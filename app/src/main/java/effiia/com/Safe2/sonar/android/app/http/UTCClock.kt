/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.http

import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime

interface UTCClock {
    fun now(): LocalDateTime = LocalDateTime.now(DateTimeZone.UTC)
}

class RealUTCClock : UTCClock
