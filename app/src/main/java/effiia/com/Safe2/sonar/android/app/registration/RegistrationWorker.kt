/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.registration

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import effiia.com.Safe2.sonar.android.app.appComponent

class RegistrationWorker(appContext: Context, private val workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val work by lazy { appComponent.registrationWork() }

    override suspend fun doWork(): Result =
        work.doWork()

    companion object {
        const val WAITING_FOR_ACTIVATION_CODE = "WAITING_FOR_ACTIVATION_CODE"
    }
}
