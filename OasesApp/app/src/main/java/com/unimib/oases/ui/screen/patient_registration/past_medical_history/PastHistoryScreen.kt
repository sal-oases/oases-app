package com.unimib.oases.ui.screen.patient_registration.past_medical_history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unimib.oases.ui.components.form.DateSelector
import com.unimib.oases.ui.components.util.AnimatedLabelOutlinedTextField
import com.unimib.oases.ui.components.util.BottomButtons
import com.unimib.oases.ui.components.util.FadeOverlay
import com.unimib.oases.ui.components.util.circularprogressindicator.CustomCircularProgressIndicator
import com.unimib.oases.ui.util.ToastUtils

@Composable
fun PastHistoryScreen(
    state: PastHistoryState,
    onEvent: (PastHistoryEvent) -> Unit,
    onSubmitted: () -> Unit,
    onBack: () -> Unit,
    confirmButtonText: String = "Next",
    cancelButtonText: String = "Back",
    readOnly: Boolean = false
) {

    val context = LocalContext.current

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            ToastUtils.showToast(context, message)
        }
        onEvent(PastHistoryEvent.ToastShown)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){

        Box(
            modifier = Modifier.weight(1f)
        ){
            if (state.error != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = state.error!!)

                    Button(
                        onClick = {
                            onEvent(PastHistoryEvent.Retry)
                        }
                    ) {
                        Text("Retry")
                    }
                }
            } else if (state.isLoading) {
                CustomCircularProgressIndicator()
            } else {
                ChronicConditionsForm(
                    state = state,
                    onEvent = onEvent,
                    readOnly = readOnly
                )
            }

            FadeOverlay(Modifier.align(Alignment.BottomCenter))
        }

        BottomButtons(
            onCancel = { onBack() },
            onConfirm = { onSubmitted() },
            cancelButtonText = cancelButtonText,
            confirmButtonText = confirmButtonText
        )
    }
}

@Composable
fun RadioButtonsInputWithDateAndText(
    label: String,
    isDiagnosed: Boolean?,
    onSelected: (Boolean) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    additionalInfo: String,
    onAdditionalInfoChange: (String) -> Unit,
    readOnly: Boolean = false,
    onReadOnlyClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton( // "Yes" button
                selected = isDiagnosed == true,
                onClick = { if (!readOnly) onSelected(true) else onReadOnlyClick()},
            )
            RadioButton( // "No"  button
                selected = isDiagnosed == false,
                onClick = { if (!readOnly) onSelected(false) else onReadOnlyClick()},
            )
        }

        if (isDiagnosed == true){
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                DateSelector(
                    selectedDate = date,
                    onDateSelected = { onDateChange(it) },
                    context = LocalContext.current,
                    readOnly = readOnly,
                    onReadOnlyClick = onReadOnlyClick
                )

                AnimatedLabelOutlinedTextField(
                    value = additionalInfo,
                    onValueChange = { onAdditionalInfoChange(it) },
                    labelText = "Additional Info",
                    readOnly = readOnly,
                    onClick = if (readOnly) onReadOnlyClick else null
                )
            }
        }
    }
}

@Composable
fun ChronicConditionsForm(
    state: PastHistoryState,
    onEvent: (PastHistoryEvent) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
){

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ){
            Text(
                text = "Is the patient affected by the following disease?",
                modifier = Modifier.weight(1f)
            )

            Text("  Yes    No  ")
        }

        HorizontalDivider()

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            for (disease in state.diseases) {
                RadioButtonsInputWithDateAndText(
                    label = disease.disease,
                    isDiagnosed = disease.isDiagnosed,
                    onSelected = { isDiagnosed ->
                        onEvent(
                            PastHistoryEvent.RadioButtonClicked(
                                disease.disease,
                                isDiagnosed
                            )
                        )
                    },
                    date = disease.date,
                    onDateChange = {
                        onEvent(
                            PastHistoryEvent.DateChanged(
                                disease.disease,
                                it
                            )
                        )
                    },
                    additionalInfo = disease.additionalInfo,
                    onAdditionalInfoChange = {
                        onEvent(
                            PastHistoryEvent.AdditionalInfoChanged(
                                disease.disease,
                                it
                            )
                        )
                    },
                    readOnly = readOnly,
                    onReadOnlyClick = {
                        onEvent(PastHistoryEvent.NurseClicked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(60.dp)) // Adds breathing room before bottom buttons
        }
    }
}