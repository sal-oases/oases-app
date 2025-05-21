package com.unimib.oases.domain.repository

import com.unimib.oases.domain.model.VitalSign
import com.unimib.oases.util.Resource
import kotlinx.coroutines.flow.Flow

interface VitalSignRepository {
    suspend fun addVitalSign(vitalSign: VitalSign): Resource<Unit>
    fun getAllVitalSigns(): Flow<Resource<List<VitalSign>>>
    fun deleteVitalSign(vitalSign: String): Resource<Unit>
}