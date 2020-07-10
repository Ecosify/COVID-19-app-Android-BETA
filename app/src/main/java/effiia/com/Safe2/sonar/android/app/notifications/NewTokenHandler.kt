/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.notifications

import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import javax.inject.Inject

class NewTokenHandler @Inject constructor(
    private val sonarIdProvider: SonarIdProvider,
    private val tokenRefreshWorkScheduler: TokenRefreshWorkScheduler
) {

    fun handle(token: String) {
        if (!sonarIdProvider.hasProperSonarId())
            return

        tokenRefreshWorkScheduler.schedule(sonarIdProvider.get(), token)
    }
}
