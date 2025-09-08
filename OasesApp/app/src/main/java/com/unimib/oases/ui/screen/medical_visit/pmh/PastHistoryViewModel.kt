package com.unimib.oases.ui.screen.medical_visit.pmh

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.oases.di.IoDispatcher
import com.unimib.oases.domain.repository.PatientDiseaseRepository
import com.unimib.oases.domain.usecase.DiseaseUseCase
import com.unimib.oases.domain.usecase.SavePastMedicalHistoryUseCase
import com.unimib.oases.ui.navigation.NavigationEvent
import com.unimib.oases.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PastHistoryViewModel @Inject constructor(
    private val diseaseUseCases: DiseaseUseCase,
    private val patientDiseaseRepository: PatientDiseaseRepository,
    private val savePastMedicalHistoryUseCase: SavePastMedicalHistoryUseCase,
    savedStateHandle: SavedStateHandle,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {

    private var errorHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        _state.value = _state.value.copy(
            error = e.message,
            isLoading = false
        )
    }

    private val _state = MutableStateFlow(PastHistoryState(
        receivedId = savedStateHandle["patientId"]!!
    ))
    val state: StateFlow<PastHistoryState> = _state.asStateFlow()

    private val navigationEventsChannel = Channel<NavigationEvent>()
    val navigationEvents = navigationEventsChannel.receiveAsFlow()

    init {
        refresh()
    }

    private fun refresh(){
        _state.value = _state.value.copy(
            error = null,
            isLoading = true
        )
        viewModelScope.launch(ioDispatcher + errorHandler) {
            loadDiseases()
            loadPatientDiseases()
        }
    }

    private suspend fun loadDiseases() {
        _state.update { it.copy(isLoading = true) }

        val sex = state.value.sex
        val age = state.value.age

        try {
            val diseasesResource = diseaseUseCases.getFilteredDiseases(sex.name, age.name).first {
                it is Resource.Success || it is Resource.Error
            }

            when (diseasesResource) {
                is Resource.Success -> {
                    val diseasesData = diseasesResource.data.orEmpty()
                    val newStates = diseasesData.map { PatientDiseaseState(it.name) }

                    _state.update {
                        it.copy(diseases = newStates, error = null)
                    }
                }

                is Resource.Error -> {
                    _state.update { it.copy(error = diseasesResource.message) }
                }

                else -> Unit
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message ?: "Unexpected error") }
        } finally {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadPatientDiseases() {
        _state.update { it.copy(isLoading = true) }

        val patientId = _state.value.receivedId

        try {
            val result = patientDiseaseRepository.getPatientDiseases(patientId).first {
                it is Resource.Success || it is Resource.Error
            }

            if (result is Resource.Success) {
                val patientDiseases = result.data.orEmpty()
                val dbMap = patientDiseases.associateBy { it.diseaseName }

                _state.update { currentState ->
                    val updatedDiseases = currentState.diseases.map { diseaseUi ->
                        dbMap[diseaseUi.disease]?.let { dbEntry ->
                            diseaseUi.copy(
                                isDiagnosed = dbEntry.isDiagnosed,
                                additionalInfo = dbEntry.additionalInfo,
                                date = dbEntry.diagnosisDate
                            )
                        } ?: diseaseUi
                    }

                    currentState.copy(diseases = updatedDiseases, isLoading = false)
                }
            } else if (result is Resource.Error) {
                _state.update { it.copy(error = result.message) }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message ?: "Unexpected error") }
        } finally {
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: PastHistoryEvent){
        when (event) {
            is PastHistoryEvent.AdditionalInfoChanged -> {
                _state.update {
                    it.copy(
                        diseases = state.value.diseases.map { d ->
                            if (d.disease == event.disease)
                                d.copy(additionalInfo = event.additionalInfo)
                            else
                                d
                        }
                    )
                }
            }
            is PastHistoryEvent.DateChanged -> {
                _state.update {
                    it.copy(
                        diseases = state.value.diseases.map { d ->
                            if (d.disease == event.disease)
                                d.copy(date = event.date)
                            else
                                d
                        }
                    )
                }
            }
            PastHistoryEvent.NurseClicked -> {
                _state.update {
                    it.copy(toastMessage = "Only doctors can edit PMH")
                }
            }
            is PastHistoryEvent.RadioButtonClicked -> {
                _state.update{
                    it.copy(
                        diseases = state.value.diseases.map { d ->
                            if (d.disease == event.disease)
                                d.copy(isDiagnosed = event.isDiagnosed)
                            else
                                d
                        }
                    )
                }
            }
            PastHistoryEvent.Retry -> {
                refresh()
            }
            PastHistoryEvent.ToastShown -> {
                _state.update {
                    it.copy(toastMessage = null)
                }
            }

            PastHistoryEvent.Cancel -> navigationEventsChannel.trySend(NavigationEvent.NavigateBack)
            PastHistoryEvent.Save -> {
                viewModelScope.launch(ioDispatcher + errorHandler) {
                    val result = savePastMedicalHistoryUseCase(state.value)
                    when (result) {
                        is Resource.Error -> _state.update { it.copy( error = result.message) }

                        is Resource.Success -> {
                            _state.update { it.copy(toastMessage = "PMH saved successfully") }
                            navigationEventsChannel.send(NavigationEvent.NavigateBack)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}