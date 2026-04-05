@file:OptIn(ExperimentalForeignApi::class)

package com.dating.home.presentation.profile.mediapicker

// NOTE: Add NSCameraUsageDescription to Info.plist before submitting to App Store.
// e.g. "We use the front camera to verify your identity."

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraDevice
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun rememberSelfieCameraLauncher(
    onResult: (PickedImageData?) -> Unit
): SelfieCameraLauncher {
    val scope = rememberCoroutineScope()

    val delegate = remember {
        object : NSObject(),
            UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {

            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                picker.dismissViewControllerAnimated(true, null)
                scope.launch {
                    val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                    if (image == null) {
                        onResult(null)
                        return@launch
                    }
                    val nsData = UIImageJPEGRepresentation(image, 0.85)
                    if (nsData == null) {
                        onResult(null)
                        return@launch
                    }
                    val bytes = ByteArray(nsData.length.toInt())
                    withContext(Dispatchers.Default) {
                        memcpy(bytes.refTo(0), nsData.bytes, nsData.length)
                    }
                    onResult(PickedImageData(bytes = bytes, mimeType = "image/jpeg"))
                }
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
                scope.launch { onResult(null) }
            }
        }
    }

    return remember {
        SelfieCameraLauncher {
            val picker = UIImagePickerController()
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            picker.cameraDevice = UIImagePickerControllerCameraDevice.UIImagePickerControllerCameraDeviceFront
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}
