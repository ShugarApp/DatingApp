package com.dating.home.presentation.emergency.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.emergency_add_contact
import aura.feature.home.presentation.generated.resources.emergency_contact_name_hint
import aura.feature.home.presentation.generated.resources.emergency_import_from_contacts
import aura.feature.home.presentation.generated.resources.emergency_contact_phone_hint
import aura.feature.home.presentation.generated.resources.emergency_contact_relationship_hint
import aura.feature.home.presentation.generated.resources.emergency_edit_contact
import aura.feature.home.presentation.generated.resources.emergency_save_contact
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.home.domain.emergency.EmergencyContact
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AddEmergencyContactBottomSheet(
    contactToEdit: EmergencyContact?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (EmergencyContact) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(contactToEdit) { mutableStateOf(contactToEdit?.name ?: "") }
    var phone by remember(contactToEdit) { mutableStateOf(contactToEdit?.phoneNumber ?: "") }
    var relationship by remember(contactToEdit) { mutableStateOf(contactToEdit?.relationship ?: "") }

    val contactPicker = rememberContactPickerLauncher { picked ->
        picked?.let {
            name = it.name
            phone = it.phoneNumber
        }
    }

    val isEditing = contactToEdit != null
    val canSave = name.isNotBlank() && phone.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditing) {
                    stringResource(Res.string.emergency_edit_contact)
                } else {
                    stringResource(Res.string.emergency_add_contact)
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = { contactPicker.launch() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Contacts,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(Res.string.emergency_import_from_contacts))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(Res.string.emergency_contact_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(Res.string.emergency_contact_phone_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = relationship,
                onValueChange = { relationship = it },
                label = { Text(stringResource(Res.string.emergency_contact_relationship_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ChirpButton(
                text = stringResource(Res.string.emergency_save_contact),
                onClick = {
                    val contact = EmergencyContact(
                        id = contactToEdit?.id ?: Uuid.random().toString(),
                        name = name.trim(),
                        phoneNumber = phone.trim(),
                        relationship = relationship.trim()
                    )
                    onSave(contact)
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
