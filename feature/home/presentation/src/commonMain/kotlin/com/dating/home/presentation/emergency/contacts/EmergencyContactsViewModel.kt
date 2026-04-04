package com.dating.home.presentation.emergency.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.location.LocationProvider
import com.dating.home.data.emergency.AudioRecorderService
import com.dating.home.data.emergency.SmsDispatcher
import com.dating.home.domain.emergency.EmergencyContact
import com.dating.home.domain.emergency.EmergencyContactRepository
import com.dating.home.domain.emergency.EmergencySettings
import com.dating.home.domain.emergency.EmergencySettingsStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmergencyContactsViewModel(
    private val repository: EmergencyContactRepository,
    private val settingsStorage: EmergencySettingsStorage,
    private val locationProvider: LocationProvider,
    private val smsDispatcher: SmsDispatcher,
    private val audioRecorderService: AudioRecorderService
) : ViewModel() {

    private val _state = MutableStateFlow(EmergencyContactsState())
    val state = combine(
        _state,
        repository.getAll(),
        settingsStorage.observe()
    ) { currentState, contacts, settings ->
        currentState.copy(
            contacts = contacts,
            autoCall911 = settings.autoCall911
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = EmergencyContactsState()
    )

    private val _events = Channel<EmergencyContactsEvent>()
    val events = _events.receiveAsFlow()

    private var sosJob: Job? = null

    fun onAction(action: EmergencyContactsAction) {
        when (action) {
            EmergencyContactsAction.OnAddContactClick -> {
                if (state.value.contacts.size >= 5) {
                    _state.update { it.copy(showMaxContactsWarning = true) }
                } else {
                    _state.update { it.copy(showAddContactSheet = true, contactToEdit = null) }
                }
            }
            EmergencyContactsAction.OnDismissAddContactSheet -> {
                _state.update { it.copy(showAddContactSheet = false, contactToEdit = null) }
            }
            is EmergencyContactsAction.OnSaveContact -> saveContact(action.contact)
            is EmergencyContactsAction.OnEditContactClick -> {
                _state.update { it.copy(showAddContactSheet = true, contactToEdit = action.contact) }
            }
            is EmergencyContactsAction.OnDeleteContactClick -> {
                _state.update { it.copy(contactToDelete = action.contact, showDeleteDialog = true) }
            }
            EmergencyContactsAction.OnConfirmDeleteContact -> deleteContact()
            EmergencyContactsAction.OnDismissDeleteDialog -> {
                _state.update { it.copy(contactToDelete = null, showDeleteDialog = false) }
            }
            is EmergencyContactsAction.OnAutoCall911Toggle -> {
                viewModelScope.launch { settingsStorage.setAutoCall911(action.enabled) }
            }
            EmergencyContactsAction.OnDismissMaxContactsWarning -> {
                _state.update { it.copy(showMaxContactsWarning = false) }
            }
            EmergencyContactsAction.OnSosTrigger -> startSosCountdown()
            EmergencyContactsAction.OnSosCancel -> cancelSos()
        }
    }

    private fun saveContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.add(contact)
            _state.update { it.copy(showAddContactSheet = false, contactToEdit = null) }
        }
    }

    private fun deleteContact() {
        val contact = _state.value.contactToDelete ?: return
        viewModelScope.launch {
            repository.delete(contact.id)
            _state.update { it.copy(contactToDelete = null, showDeleteDialog = false) }
        }
    }

    fun startSosCountdown() {
        _state.update { it.copy(sosCountdown = 5, showSosCountdown = true) }
        sosJob = viewModelScope.launch {
            for (i in 4 downTo 0) {
                delay(1000L)
                if (_state.value.showSosCountdown) {
                    _state.update { it.copy(sosCountdown = i) }
                } else {
                    return@launch
                }
            }
            executeSos()
        }
    }

    fun cancelSos() {
        sosJob?.cancel()
        sosJob = null
        _state.update { it.copy(showSosCountdown = false, sosCountdown = 5) }
    }

    private fun executeSos() {
        _state.update { it.copy(showSosCountdown = false, isSendingSos = true) }
        viewModelScope.launch {
            val location = locationProvider.getLastKnownLocation()
            val locationText = if (location != null) {
                "https://maps.google.com/?q=${location.latitude},${location.longitude}"
            } else {
                "Location unavailable"
            }

            val contacts = state.value.contacts
            val message = "SOS! I may be in danger. My location: $locationText — Sent via Aura safety feature."

            var sentCount = 0
            contacts.forEach { contact ->
                val sent = smsDispatcher.send(contact.phoneNumber, message)
                if (sent) sentCount++
            }

            if (!audioRecorderService.isRecording()) {
                try {
                    audioRecorderService.start("/tmp/sos_recording_${System.currentTimeMillis()}.m4a")
                } catch (_: Exception) {}
            }

            _state.update { it.copy(isSendingSos = false) }
            _events.send(EmergencyContactsEvent.SosSent(sentCount))

            if (state.value.autoCall911) {
                _events.send(EmergencyContactsEvent.Call911)
            }
        }
    }
}

data class EmergencyContactsState(
    val contacts: List<EmergencyContact> = emptyList(),
    val autoCall911: Boolean = false,
    val showAddContactSheet: Boolean = false,
    val contactToEdit: EmergencyContact? = null,
    val showDeleteDialog: Boolean = false,
    val contactToDelete: EmergencyContact? = null,
    val showMaxContactsWarning: Boolean = false,
    val showSosCountdown: Boolean = false,
    val sosCountdown: Int = 5,
    val isSendingSos: Boolean = false
)

sealed interface EmergencyContactsAction {
    data object OnAddContactClick : EmergencyContactsAction
    data object OnDismissAddContactSheet : EmergencyContactsAction
    data class OnSaveContact(val contact: EmergencyContact) : EmergencyContactsAction
    data class OnEditContactClick(val contact: EmergencyContact) : EmergencyContactsAction
    data class OnDeleteContactClick(val contact: EmergencyContact) : EmergencyContactsAction
    data object OnConfirmDeleteContact : EmergencyContactsAction
    data object OnDismissDeleteDialog : EmergencyContactsAction
    data class OnAutoCall911Toggle(val enabled: Boolean) : EmergencyContactsAction
    data object OnDismissMaxContactsWarning : EmergencyContactsAction
    data object OnSosTrigger : EmergencyContactsAction
    data object OnSosCancel : EmergencyContactsAction
}

sealed interface EmergencyContactsEvent {
    data class SosSent(val contactCount: Int) : EmergencyContactsEvent
    data object Call911 : EmergencyContactsEvent
}
