/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.registration

sealed class RegistrationResult {
    object Success : RegistrationResult()
    object WaitingForActivationCode : RegistrationResult()
    object Error : RegistrationResult()
}
