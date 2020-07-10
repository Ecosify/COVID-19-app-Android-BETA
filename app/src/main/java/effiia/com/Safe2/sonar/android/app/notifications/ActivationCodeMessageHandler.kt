/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.notifications

import effiia.com.Safe2.sonar.android.app.registration.ActivationCodeProvider
import effiia.com.Safe2.sonar.android.app.registration.RegistrationManager
import javax.inject.Inject

class ActivationCodeMessageHandler @Inject constructor(
    private val activationCodeProvider: ActivationCodeProvider,
    private val registrationManager: RegistrationManager
) {

    fun handle(message: ActivationCodeMessage) {
        activationCodeProvider.set(message.code)
        registrationManager.register()
    }
}
