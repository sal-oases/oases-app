package com.unimib.oases.ui.screen.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.oases.data.bluetooth.BluetoothCustomManager
import com.unimib.oases.di.IoDispatcher
import com.unimib.oases.domain.model.BluetoothEvent
import com.unimib.oases.ui.components.scaffold.UiEvent
import com.unimib.oases.ui.navigation.NavigationEvent
import com.unimib.oases.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val bluetoothManager: BluetoothCustomManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _navEvents = MutableSharedFlow<NavigationEvent>()
    val navEvents: SharedFlow<NavigationEvent> = _navEvents.asSharedFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    init {
        observeBluetoothEvents()
    }

    private fun observeBluetoothEvents() {
        viewModelScope.launch(ioDispatcher) {
            bluetoothManager.events.collect { event ->
                when (event) {
                    is BluetoothEvent.PatientReceived -> {
                        showSnackbar(
                            message = "${event.patientFullData.patientDetails.name} was successfully sent to this device",
                            actionLabel = "View",
                            onAction = { navigateTo(Screen.PatientDashboard.route + "/patientId=${event.patientFullData.patientDetails.id}") }
                        )
                    }

                    is BluetoothEvent.ErrorWhileReceivingPatient -> {
                        showSnackbar(
                            message = event.errorMessage
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(route: String) {
        viewModelScope.launch {
            _navEvents.emit(NavigationEvent.Navigate(route))
        }
    }

    fun onNavEvent(event: NavigationEvent){
        viewModelScope.launch { _navEvents.emit(event) }
    }

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar(message, actionLabel, onAction))
        }
    }

}