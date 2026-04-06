package com.dating.home.presentation.emergency.contacts

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberContactPickerLauncher(
    onResult: (PickedContactData?) -> Unit
): ContactPickerLauncher {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }
        val uri = result.data?.data
        if (uri == null) {
            onResult(null)
            return@rememberLauncherForActivityResult
        }
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(0).orEmpty()
                val phone = it.getString(1).orEmpty()
                onResult(PickedContactData(name = name, phoneNumber = phone))
            } else {
                onResult(null)
            }
        } ?: onResult(null)
    }

    return remember {
        ContactPickerLauncher {
            val intent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            launcher.launch(intent)
        }
    }
}
