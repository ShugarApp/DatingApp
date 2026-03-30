@file:OptIn(ExperimentalForeignApi::class)

package com.dating.home.presentation.chat.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dating.home.presentation.profile.mediapicker.PickedImageData
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
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_group_create
import platform.darwin.dispatch_group_enter
import platform.darwin.dispatch_group_leave
import platform.darwin.dispatch_group_notify
import platform.posix.memcpy

@Composable
actual fun rememberMediaPickerLauncher(
    mediaFilter: MediaFilter,
    onResult: (PickedImageData) -> Unit
): MediaPickerLauncher {
    val scope = rememberCoroutineScope()

    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)

                val results = didFinishPicking.filterIsInstance<PHPickerResult>()

                val dispatchGroup = dispatch_group_create()
                val dataList = mutableListOf<PickedImageData>()

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
                    ) { nsData, _ ->
                        scope.launch {
                            nsData?.let {
                                val bytes = ByteArray(it.length.toInt())
                                withContext(Dispatchers.Default) {
                                    memcpy(bytes.refTo(0), it.bytes, it.length)
                                }
                                dataList.add(
                                    PickedImageData(
                                        bytes = bytes,
                                        mimeType = mimeType
                                    )
                                )
                            }
                            dispatch_group_leave(dispatchGroup)
                        }
                    }

                    dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
                        scope.launch {
                            dataList.firstOrNull()?.let { item ->
                                onResult(item)
                            }
                        }
                    }
                }
            }
        }
    }

    return remember(mediaFilter) {
        val filter = when (mediaFilter) {
            MediaFilter.IMAGES_AND_GIFS -> PHPickerFilter.imagesFilter
            MediaFilter.AUDIO -> PHPickerFilter.anyFilterMatchingSubfilters(
                listOf(
                    PHPickerFilter.filter(UTType.audio)
                )
            )
        }

        val pickerViewController = PHPickerViewController(
            configuration = PHPickerConfiguration().apply {
                setSelectionLimit(1)
                setFilter(filter)
                setSelection(PHPickerConfigurationSelectionOrdered)
            }
        )
        pickerViewController.delegate = delegate

        MediaPickerLauncher(
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
