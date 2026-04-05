package com.dating.aura

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.dating.aura.navigation.ExternalUriHandler
import com.dating.core.domain.auth.SessionStorage
import com.dating.home.data.emergency.VolumeButtonEventBus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val sessionStorage: SessionStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        var shouldShowSplashScreen = true

        installSplashScreen().setKeepOnScreenCondition {
            shouldShowSplashScreen
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        handleNotificationDeeplink(intent)

        setContent {
            App(
                onAuthenticationChecked = {
                    shouldShowSplashScreen = false
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationDeeplink(intent)
    }

    // Volume button x3 SOS trigger
    private var volumePressCount = 0
    private var lastVolumePressTime = 0L

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN &&
            (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || event.keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        ) {
            val now = System.currentTimeMillis()
            if (now - lastVolumePressTime > 2000L) {
                volumePressCount = 0
            }
            volumePressCount++
            lastVolumePressTime = now
            if (volumePressCount >= 3) {
                volumePressCount = 0
                VolumeButtonEventBus.emit()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun handleNotificationDeeplink(intent: Intent) {
        val type = intent.getStringExtra("type") ?: intent.extras?.getString("type")

        when (type) {
            "new_message" -> {
                val chatId = intent.getStringExtra("chatId") ?: intent.extras?.getString("chatId")
                if (chatId != null) {
                    ExternalUriHandler.onNewUri("aura://chat_detail/$chatId")
                }
            }

            "match_created" -> {
                val user1Id = intent.getStringExtra("user1Id") ?: intent.extras?.getString("user1Id")
                val user2Id = intent.getStringExtra("user2Id") ?: intent.extras?.getString("user2Id")
                lifecycleScope.launch {
                    val currentUserId = sessionStorage.observeAuthInfo().first()?.user?.id
                    val matchedUserId = when {
                        user1Id == currentUserId -> user2Id
                        user2Id == currentUserId -> user1Id
                        else -> null
                    }
                    ExternalUriHandler.onNewUri("chirp://home?section=matches")
                    // matchedUserId is available here for future use (e.g. open profile directly)
                }
            }

            else -> {
                // Legacy: notifications sent before type field was introduced
                val chatId = intent.getStringExtra("chatId") ?: intent.extras?.getString("chatId")
                if (chatId != null) {
                    ExternalUriHandler.onNewUri("aura://chat_detail/$chatId")
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
