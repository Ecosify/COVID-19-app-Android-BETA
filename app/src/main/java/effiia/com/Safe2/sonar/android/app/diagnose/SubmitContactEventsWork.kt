/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.diagnose

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.workDataOf
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import effiia.com.Safe2.sonar.android.app.contactevents.CoLocationDataProvider
import effiia.com.Safe2.sonar.android.app.diagnose.review.CoLocationApi
import effiia.com.Safe2.sonar.android.app.diagnose.review.CoLocationData
import effiia.com.Safe2.sonar.android.app.functionaltypes.runSafely
import effiia.com.Safe2.sonar.android.app.registration.SonarIdProvider
import effiia.com.Safe2.sonar.android.app.status.Symptom
import effiia.com.Safe2.sonar.android.app.util.toUtcIsoFormat
import javax.inject.Inject

class SubmitContactEventsWork @Inject constructor(
    private val coLocationApi: CoLocationApi,
    private val coLocationDataProvider: CoLocationDataProvider,
    private val sonarIdProvider: SonarIdProvider
) {

    companion object {
        const val SYMPTOMS_DATE = "SYMPTOMS_DATE"
        const val SYMPTOMS = "SYMPTOMS"

        fun data(symptomsDate: LocalDate, symptoms: List<Symptom>): Data {
            val symptomsDateUtcIsoFormat: String =
                symptomsDate.toDateTime(LocalTime.now(), DateTimeZone.UTC).toUtcIsoFormat()
            val symptomsArray = symptoms.map { it.value }.toTypedArray()

            return workDataOf(SYMPTOMS_DATE to symptomsDateUtcIsoFormat, SYMPTOMS to symptomsArray)
        }
    }

    suspend fun doWork(data: Data): ListenableWorker.Result =
        runSafely { saveEvents(data) }
            .map { ListenableWorker.Result.success() }
            .orElse { ListenableWorker.Result.retry() }

    private suspend fun saveEvents(data: Data) {
        val contactEvents = coLocationDataProvider.getEvents()

        val symptomsTimestamp = data.getString(SYMPTOMS_DATE)!!
        val symptomsArray = data.getStringArray(SYMPTOMS)!!
        val symptoms = symptomsArray.mapNotNull { Symptom.fromValue(it) }

        val coLocationData = CoLocationData(
            sonarId = sonarIdProvider.get(),
            symptomsTimestamp = symptomsTimestamp,
            symptoms = symptoms,
            contactEvents = contactEvents
        )

        coLocationApi.save(coLocationData).toCoroutine()
        coLocationDataProvider.clearData()
    }
}
