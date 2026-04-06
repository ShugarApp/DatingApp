package com.dating.home.data.emergency

import com.dating.home.domain.emergency.VolumeButtonEventBus as DomainVolumeButtonEventBus
import kotlinx.coroutines.flow.SharedFlow

object VolumeButtonEventBus {
    val events: SharedFlow<Unit> = DomainVolumeButtonEventBus.events
    fun emit() = DomainVolumeButtonEventBus.emit()
}
