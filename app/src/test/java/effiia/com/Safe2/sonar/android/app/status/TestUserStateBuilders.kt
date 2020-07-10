/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package effiia.com.Safe2.sonar.android.app.status

import org.joda.time.DateTime
import org.joda.time.LocalDate
import effiia.com.Safe2.sonar.android.app.util.NonEmptySet
import effiia.com.Safe2.sonar.android.app.util.nonEmptySetOf
import effiia.com.Safe2.sonar.android.app.util.toUtcNormalized

fun buildExposedState(
    since: DateTime = LocalDate.now().toUtcNormalized(),
    until: DateTime = LocalDate.now().plusDays(14).toUtcNormalized()
) = ExposedState(since, until)

fun buildSymptomaticState(
    since: DateTime = LocalDate.now().toUtcNormalized(),
    until: DateTime = LocalDate.now().plusDays(7).toUtcNormalized(),
    symptoms: NonEmptySet<Symptom> = nonEmptySetOf(Symptom.COUGH)
) = SymptomaticState(since, until, symptoms)

fun buildExposedSymptomaticState(
    since: DateTime = LocalDate.now().toUtcNormalized(),
    until: DateTime = LocalDate.now().plusDays(7).toUtcNormalized(),
    exposedAt: DateTime = since,
    symptoms: NonEmptySet<Symptom> = nonEmptySetOf(Symptom.COUGH)
) = ExposedSymptomaticState(since, until, exposedAt, symptoms)

fun buildPositiveState(
    since: DateTime = LocalDate.now().toUtcNormalized(),
    until: DateTime = LocalDate.now().plusDays(7).toUtcNormalized(),
    symptoms: NonEmptySet<Symptom> = nonEmptySetOf(Symptom.COUGH)
) = PositiveState(since, until, symptoms)
