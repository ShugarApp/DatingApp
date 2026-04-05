package com.dating.home.presentation.profile.mediapicker

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// NOTE: Add android.permission.CAMERA to AndroidManifest.xml if you want to request it
// proactively. ACTION_IMAGE_CAPTURE itself routes to the camera app which holds the permission.
@Composable
actual fun rememberSelfieCameraLauncher(
    onResult: (PickedImageData?) -> Unit
): SelfieCameraLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var captureUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = TakeSelfieContract()
    ) { success ->
        if (success) {
            scope.launch {
                val uri = captureUri ?: run { onResult(null); return@launch }
                val bytes = withContext(Dispatchers.IO) {
                    try { context.contentResolver.openInputStream(uri)?.use { it.readBytes() } }
                    catch (_: Exception) { null }
                }
                onResult(
                    if (bytes != null) PickedImageData(bytes, "image/jpeg") else null
                )
            }
        } else {
            onResult(null)
        }
    }

    return remember {
        SelfieCameraLauncher {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "selfie_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            captureUri = uri
            if (uri != null) launcher.launch(uri)
        }
    }
}

private class TakeSelfieContract : ActivityResultContract<android.net.Uri, Boolean>() {
    override fun createIntent(context: Context, input: android.net.Uri): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, input)
            // Hints to request the front-facing camera (honored by most camera apps)
            putExtra("android.intent.extras.CAMERA_FACING", 1)
            putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}
