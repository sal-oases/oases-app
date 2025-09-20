package com.unimib.oases.domain.usecase

import com.unimib.oases.domain.model.symptom.SymptomTriageCode
import com.unimib.oases.domain.model.symptom.TriageSymptom
import com.unimib.oases.ui.screen.nurse_assessment.triage.SymptomWithLabel
import com.unimib.oases.ui.screen.nurse_assessment.triage.TriageConfig
import javax.inject.Inject

class ConfigTriageUseCase @Inject constructor(
    private val getPatientCategoryUseCase: GetPatientCategoryUseCase
) {
    operator fun invoke(ageInMonths: Int): TriageConfig {

        val category = getPatientCategoryUseCase(ageInMonths)

        val redOptions = mutableListOf<SymptomWithLabel>()
        val yellowOptions = mutableListOf<SymptomWithLabel>()

        TriageSymptom.entries.forEach {
            val symptom = SymptomWithLabel(it, it.labelFor(category))
            when (it.colorAssigner(category)) {
                SymptomTriageCode.RED -> redOptions.add(symptom)
                SymptomTriageCode.YELLOW -> yellowOptions.add(symptom)
                null -> {}
            }
        }

        return TriageConfig(
            redOptions.toList(),
            yellowOptions.toList()
        )
    }
}