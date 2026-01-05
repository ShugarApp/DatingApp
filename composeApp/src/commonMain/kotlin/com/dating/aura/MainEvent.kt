package com.dating.aura

sealed interface MainEvent {
    data object OnSessionExpired: MainEvent
}