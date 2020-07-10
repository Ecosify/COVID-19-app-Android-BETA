/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.di.module

import dagger.Module
import dagger.Provides
import effiia.com.Safe2.sonar.android.app.registration.FirebaseTokenRetriever
import effiia.com.Safe2.sonar.android.app.registration.TokenRetriever

@Module
class NotificationsModule {

    @Provides
    fun provideTokenRetriever(implementation: FirebaseTokenRetriever): TokenRetriever =
        implementation
}
