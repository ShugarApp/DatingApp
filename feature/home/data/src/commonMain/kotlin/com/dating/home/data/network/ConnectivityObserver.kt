package com.dating.home.data.network

import kotlinx.coroutines.flow.Flow

expect class ConnectivityObserver {
    val isConnected: Flow<Boolean>
}
