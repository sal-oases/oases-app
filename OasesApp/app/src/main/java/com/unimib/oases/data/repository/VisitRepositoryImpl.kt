package com.unimib.oases.data.repository

import android.util.Log
import com.unimib.oases.data.local.RoomDataSource
import com.unimib.oases.data.mapper.toDomain
import com.unimib.oases.data.mapper.toEntity
import com.unimib.oases.domain.model.Visit
import com.unimib.oases.domain.repository.VisitRepository
import com.unimib.oases.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class VisitRepositoryImpl @Inject constructor(
    private val roomDataSource: RoomDataSource,
): VisitRepository {

    override suspend fun addVisit(visit: Visit): Resource<Unit> {
        return try {
            roomDataSource.insertVisit(visit.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("VisitRepository", "Error adding visit: ${e.message}")
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun updateVisit(visit: Visit): Resource<Unit> {
        return try {
            roomDataSource.upsertVisit(visit.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("VisitRepository", "Error updating visit: ${e.message}")
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override fun getVisits(patientId: String): Flow<Resource<List<Visit>>> = flow {
//        if (Random.nextBoolean())
//            emit(Resource.Error("Mock error"))
//        else
            roomDataSource.getVisits(patientId)
                .onStart { emit(Resource.Loading()) }
                .catch {
                    Log.e("VisitRepository", "Error getting visits: ${it.message}")
                    emit(Resource.Error(it.message ?: "An error occurred"))
                }
                .collect {
                    emit(Resource.Success(it.map { entity -> entity.toDomain() }))
                }
    }

    override fun getCurrentVisit(patientId: String): Flow<Resource<Visit?>> = flow {
//        if (Random.nextBoolean())
//            emit(Resource.Error("Mock error"))
//        else
            roomDataSource.getCurrentVisit(patientId)
                .onStart {
                    emit(Resource.Loading())
                }
                .catch {
                    Log.e("VisitRepository", "Error getting current visit: ${it.message}")
                    emit(Resource.Error(it.message ?: "An error occurred"))
                }
                .collect { entity ->
                    val domainModel = entity?.toDomain()
                    emit(Resource.Success(domainModel))
                }
    }
}