/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.notifications

import androidx.room.Entity
import androidx.room.PrimaryKey
import effiia.com.Safe2.sonar.android.app.notifications.Acknowledgment.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Acknowledgment(@PrimaryKey val url: String) {
    companion object {
        const val TABLE_NAME = "acknowledgments"
    }
}
