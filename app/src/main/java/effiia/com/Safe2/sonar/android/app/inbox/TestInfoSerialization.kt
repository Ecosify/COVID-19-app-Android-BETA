/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.inbox

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.json.JSONObject
import effiia.com.Safe2.sonar.android.app.http.jsonOf

object TestInfoSerialization {

    fun serialize(info: TestInfo): String =
        jsonOf(
            "result" to info.result.name,
            "date" to info.date.millis
        )

    fun deserialize(json: String?): TestInfo {
        if (json == null) throw IllegalArgumentException("null json object")

        return JSONObject(json).let {
            TestInfo(
                result = TestResult.valueOf(it.getString("result")),
                date = DateTime(it.getLong("date"), DateTimeZone.UTC)
            )
        }
    }
}
