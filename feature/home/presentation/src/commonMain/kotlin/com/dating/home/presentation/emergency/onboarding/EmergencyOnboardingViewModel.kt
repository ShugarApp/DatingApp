package com.dating.home.presentation.emergency.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.home.domain.emergency.EmergencySettingsStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EmergencyOnboardingViewModel(
    private val emergencySettingsStorage: EmergencySettingsStorage
) : ViewModel() {

    private val _events = Channel<EmergencyOnboardingEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: EmergencyOnboardingAction) {
        when (action) {
            EmergencyOnboardingAction.OnFinish -> markSeenAndFinish()
            EmergencyOnboardingAction.OnSkip -> markSeenAndFinish()
        }
    }

    private fun markSeenAndFinish() {
        viewModelScope.launch {
            emergencySettingsStorage.setOnboardingSeen(true)
            _events.send(EmergencyOnboardingEvent.OnFinished)
        }
    }
}

sealed interface EmergencyOnboardingAction {
    data object OnFinish : EmergencyOnboardingAction
    data object OnSkip : EmergencyOnboardingAction
}

sealed interface EmergencyOnboardingEvent {
    data object OnFinished : EmergencyOnboardingEvent
}
