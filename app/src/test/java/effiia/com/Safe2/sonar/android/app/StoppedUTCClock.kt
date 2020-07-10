/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app

import org.joda.time.LocalDateTime
import effiia.com.Safe2.sonar.android.app.http.UTCClock

class StoppedUTCClock(private val alwaysNow: LocalDateTime) :
    UTCClock {
    override fun now(): LocalDateTime {
        return alwaysNow
    }
}
