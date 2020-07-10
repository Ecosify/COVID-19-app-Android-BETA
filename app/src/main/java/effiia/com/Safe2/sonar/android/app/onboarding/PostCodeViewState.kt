/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.onboarding

sealed class PostCodeViewState {
    object Valid : PostCodeViewState()
    object Invalid : PostCodeViewState()
}
