package com.dating.home.domain.upload

import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.home.domain.user.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotoUploadRequest(
    val bytes: ByteArray,
    val mimeType: String,
    val slotIndex: Int
)

sealed interface PhotoUploadEvent {
    data class Success(val slotIndex: Int, val publicUrl: String) : PhotoUploadEvent
    data class Failed(val slotIndex: Int) : PhotoUploadEvent
}

class PhotoUploadManager(
    private val userService: UserService,
    private val sessionStorage: SessionStorage,
    private val appScope: CoroutineScope
) {
    private val _events = MutableSharedFlow<PhotoUploadEvent>()
    val events: SharedFlow<PhotoUploadEvent> = _events

    private val _pendingSlots = MutableStateFlow<Set<Int>>(emptySet())
    val pendingSlots: StateFlow<Set<Int>> = _pendingSlots

    fun enqueue(requests: List<PhotoUploadRequest>) {
        val sorted = requests.sortedBy { it.slotIndex }
        // Mark all slots as pending upfront so the UI shows loading immediately
        sorted.forEach { request -> _pendingSlots.update { slots -> slots + request.slotIndex } }
        appScope.launch {
            // Process sequentially so lower indices are confirmed before higher ones
            for (request in sorted) {
                userService.uploadPhoto(
                    imageBytes = request.bytes,
                    mimeType = request.mimeType,
                    index = request.slotIndex
                ).onSuccess { publicUrl ->
                    updateSessionPhoto(request.slotIndex, publicUrl)
                    _events.emit(PhotoUploadEvent.Success(request.slotIndex, publicUrl))
                }.onFailure {
                    _events.emit(PhotoUploadEvent.Failed(request.slotIndex))
                }
                _pendingSlots.update { it - request.slotIndex }
            }
        }
    }

    private suspend fun updateSessionPhoto(slotIndex: Int, publicUrl: String) {
        sessionStorage.observeAuthInfo().firstOrNull()?.let { info ->
            val photos = info.user.photos.toMutableList()
            // Expand list if needed
            while (photos.size <= slotIndex) photos.add("")
            photos[slotIndex] = publicUrl
            sessionStorage.set(info.copy(user = info.user.copy(photos = photos)))
        }
    }
}
