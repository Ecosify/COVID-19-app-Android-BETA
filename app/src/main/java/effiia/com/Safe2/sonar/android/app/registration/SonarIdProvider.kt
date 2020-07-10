/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.registration

import android.content.Context
import androidx.lifecycle.LiveData
import effiia.com.Safe2.sonar.android.app.util.SharedPreferenceStringLiveData
import effiia.com.Safe2.sonar.android.app.util.SharedPreferenceStringProvider
import effiia.com.Safe2.sonar.android.app.util.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SonarIdProvider @Inject constructor(context: Context) :
    SharedPreferenceStringProvider(context, PREF_NAME, PREF_KEY) {

    fun hasProperSonarId(): Boolean =
        get().isNotEmpty()

    fun hasProperSonarIdLiveData(): LiveData<Boolean> =
        SharedPreferenceStringLiveData(sharedPreferences, PREF_KEY, "")
            .map { sonarId -> sonarId.isNotEmpty() }

    companion object {
        private const val PREF_NAME = "residentId"
        private const val PREF_KEY = "RESIDENT_ID"
    }
}
