/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import effiia.com.Safe2.sonar.android.app.contactevents.ContactEvent
import effiia.com.Safe2.sonar.android.app.contactevents.ContactEventDao
import effiia.com.Safe2.sonar.android.app.notifications.Acknowledgment
import effiia.com.Safe2.sonar.android.app.notifications.AcknowledgmentsDao

@Database(
    entities = [ContactEvent::class, Acknowledgment::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactEventDao(): ContactEventDao
    abstract fun acknowledgmentsDao(): AcknowledgmentsDao
}

class Converters {

    @TypeConverter
    fun intListToString(value: List<Int>) = value.joinToString(separator = ",")

    @TypeConverter
    fun longListToString(value: List<Long>) = value.joinToString(separator = ",")

    @TypeConverter
    fun stringToIntList(value: String) = value.split(",").map { it.toInt() }

    @TypeConverter
    fun stringToLongList(value: String) = value.split(",").map { it.toLong() }
}
