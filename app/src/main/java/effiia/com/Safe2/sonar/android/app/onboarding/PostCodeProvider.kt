/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.onboarding

import android.content.Context
import effiia.com.Safe2.sonar.android.app.util.SharedPreferenceStringProvider
import javax.inject.Inject

class PostCodeProvider @Inject constructor(context: Context) :
    SharedPreferenceStringProvider(
        context,
        preferenceName = "postCode",
        preferenceKey = "POST_CODE"
    )
