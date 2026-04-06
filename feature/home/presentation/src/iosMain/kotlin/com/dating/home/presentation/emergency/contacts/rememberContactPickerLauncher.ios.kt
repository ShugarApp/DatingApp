@file:OptIn(ExperimentalForeignApi::class)

package com.dating.home.presentation.emergency.contacts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Contacts.CNContact
import platform.Contacts.CNLabeledValue
import platform.Contacts.CNPhoneNumber
import platform.ContactsUI.CNContactPickerDelegateProtocol
import platform.ContactsUI.CNContactPickerViewController
import platform.UIKit.UIApplication
import platform.darwin.NSObject

@Composable
actual fun rememberContactPickerLauncher(
    onResult: (PickedContactData?) -> Unit
): ContactPickerLauncher {
    val delegate = remember {
        object : NSObject(), CNContactPickerDelegateProtocol {
            override fun contactPicker(
                picker: CNContactPickerViewController,
                didSelectContact: CNContact
            ) {
                picker.dismissViewControllerAnimated(true, null)
                val name = listOf(didSelectContact.givenName, didSelectContact.familyName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                val phoneNumber = (didSelectContact.phoneNumbers.firstOrNull() as? CNLabeledValue)
                    ?.value?.let { it as? CNPhoneNumber }?.stringValue.orEmpty()
                onResult(PickedContactData(name = name, phoneNumber = phoneNumber))
            }

            override fun contactPickerDidCancel(picker: CNContactPickerViewController) {
                picker.dismissViewControllerAnimated(true, null)
                onResult(null)
            }
        }
    }

    return remember {
        ContactPickerLauncher {
            val picker = CNContactPickerViewController()
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}
