/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import effiia.com.Safe2.sonar.android.app.contactevents.CoLocationDataProvider
import effiia.com.Safe2.sonar.android.app.contactevents.ContactEventDao
import effiia.com.Safe2.sonar.android.app.notifications.AcknowledgmentsDao
import effiia.com.Safe2.sonar.android.app.storage.AppDatabase

@Module
class PersistenceModule(private val appContext: Context) {

    @Provides
    fun provideDatabase() =
        Room
            .databaseBuilder(appContext, AppDatabase::class.java, "event-database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideContactEventDao(database: AppDatabase): ContactEventDao =
        database.contactEventDao()

    @Provides
    fun provideCoLocationDataProvider(dao: ContactEventDao): CoLocationDataProvider =
        CoLocationDataProvider(dao)

    @Provides
    fun provideAcknowledgmentsDao(database: AppDatabase): AcknowledgmentsDao =
        database.acknowledgmentsDao()
}
