/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import effiia.com.Safe2.sonar.android.app.onboarding.OnboardingStatusProvider
import effiia.com.Safe2.sonar.android.app.registration.RegistrationManager
import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import effiia.com.Safe2.sonar.android.app.status.RegistrationState.Complete
import effiia.com.Safe2.sonar.android.app.util.map
import javax.inject.Inject

class StatusViewModel @Inject constructor(
    private val onboardingStatusProvider: OnboardingStatusProvider,
    private val sonarIdProvider: SonarIdProvider,
    private val registrationManager: RegistrationManager
) : ViewModel() {

    fun viewState(): LiveData<RegistrationState> =
        sonarIdProvider
            .hasProperSonarIdLiveData()
            .map { hasProperSonarId ->
                if (hasProperSonarId) Complete else Complete
            }

    fun onStart() {
        if (!onboardingStatusProvider.get()) {
            onboardingStatusProvider.set(true)
        }

        if (!sonarIdProvider.hasProperSonarId()) {
            registrationManager.register()
        }
    }
}

enum class RegistrationState {
    Complete,
    InProgress
}
