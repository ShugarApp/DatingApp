package app.shugar.shugar

sealed interface MainEvent {
    data object OnSessionExpired: MainEvent
}