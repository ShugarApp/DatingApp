@file:OptIn(ExperimentalForeignApi::class)

package com.dating.home.presentation.profile.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UniformTypeIdentifiers.UTType
import platform.darwin.NSObject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_group_create
import platform.darwin.dispatch_group_enter
import platform.darwin.dispatch_group_leave
import platform.darwin.dispatch_group_notify
import platform.posix.memcpy

@Composable
actual fun rememberMultiImagePickerLauncher(
    maxSelection: Int,
    onResult: (List<PickedImageData>) -> Unit
): ImagePickerLauncher {
    val scope = rememberCoroutineScope()
    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)

                val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                if (results.isEmpty()) return

                val dispatchGroup = dispatch_group_create()
                val imageDataList = mutableListOf<PickedImageData>()
                val mutex = Mutex()

                for (result in results) {
                    dispatch_group_enter(dispatchGroup)

                    val itemProvider = result.itemProvider
                    val typeIdentifiers = itemProvider.registeredTypeIdentifiers
                    val primaryType = typeIdentifiers.firstOrNull() as? String

                    if (primaryType == null) {
                        dispatch_group_leave(dispatchGroup)
                        continue
                    }

                    val mimeType = UTType
                        .typeWithIdentifier(primaryType)
                        ?.preferredMIMEType

                    if (mimeType == null) {
                        dispatch_group_leave(dispatchGroup)
                        continue
                    }

                    itemProvider.loadDataRepresentationForTypeIdentifier(
                        typeIdentifier = primaryType
                    ) { nsData: NSData?, _: NSError? ->
                        scope.launch {
                            nsData?.let { data ->
                                val bytes = ByteArray(data.length.toInt())
                                withContext(Dispatchers.Default) {
                                    memcpy(bytes.refTo(0), data.bytes, data.length)
                                }
                                mutex.withLock {
                                    imageDataList.add(
                                        PickedImageData(bytes = bytes, mimeType = mimeType)
                                    )
                                }
                            }
                            dispatch_group_leave(dispatchGroup)
                        }
                    }
                }

                dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
                    scope.launch {
                        if (imageDataList.isNotEmpty()) {
                            onResult(imageDataList.toList())
                        }
                    }
                }
            }
        }
    }

    return remember {
        val pickerViewController = PHPickerViewController(
            configuration = PHPickerConfiguration().apply {
                setSelectionLimit(maxSelection.toLong())
                setFilter(PHPickerFilter.imagesFilter)
                setSelection(PHPickerConfigurationSelectionOrdered)
            }
        )
        pickerViewController.delegate = delegate

        ImagePickerLauncher(
            onLaunch = {
                UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                    pickerViewController,
                    true,
                    null
                )
            }
        )
    }
}
