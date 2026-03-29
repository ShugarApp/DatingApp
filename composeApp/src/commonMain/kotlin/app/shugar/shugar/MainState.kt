package app.shugar.shugar

import com.dating.core.domain.auth.UserStatus

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val userStatus: UserStatus? = null
)
