package com.dating.home.presentation.emergency.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.emergency_add_contact
import aura.feature.home.presentation.generated.resources.emergency_auto_call_911
import aura.feature.home.presentation.generated.resources.emergency_auto_call_911_desc
import aura.feature.home.presentation.generated.resources.emergency_contacts_add_first
import aura.feature.home.presentation.generated.resources.emergency_contacts_empty_desc
import aura.feature.home.presentation.generated.resources.emergency_contacts_empty_title
import aura.feature.home.presentation.generated.resources.emergency_contacts_of_max
import aura.feature.home.presentation.generated.resources.emergency_contacts_title
import aura.feature.home.presentation.generated.resources.emergency_delete_contact_confirm
import aura.feature.home.presentation.generated.resources.emergency_delete_contact_desc
import aura.feature.home.presentation.generated.resources.emergency_delete_contact_title
import aura.feature.home.presentation.generated.resources.sos_sent
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import androidx.compose.runtime.LaunchedEffect
import com.dating.core.presentation.util.ObserveAsEvents
import com.dating.home.domain.emergency.EmergencyContact
import com.dating.home.presentation.emergency.sos.SosCountdownDialog
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(
    onBack: () -> Unit,
    onCall911: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmergencyContactsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val smsPermissionLauncher = rememberSmsPermissionLauncher { /* permission result handled by OS */ }
    LaunchedEffect(Unit) { smsPermissionLauncher.launch() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EmergencyContactsEvent.SosSent -> {
                snackbarHostState.showSnackbar(getString(Res.string.sos_sent, event.contactCount))
            }
            EmergencyContactsEvent.Call911 -> onCall911()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.emergency_contacts_title),
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.contacts.size < 5) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onAction(EmergencyContactsAction.OnAddContactClick) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    },
                    text = { Text(stringResource(Res.string.emergency_add_contact)) }
                )
            }
        }
    ) { paddingValues ->
        if (state.contacts.isEmpty()) {
            // Empty state: fill space with centered content, auto-call at bottom
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmergencyContactsEmptyState(
                        onAddClick = { viewModel.onAction(EmergencyContactsAction.OnAddContactClick) },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
                AutoCall911Section(
                    isEnabled = state.autoCall911,
                    onToggle = { viewModel.onAction(EmergencyContactsAction.OnAutoCall911Toggle(it)) }
                )
                Spacer(modifier = Modifier.height(88.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Count indicator
                Text(
                    text = stringResource(Res.string.emergency_contacts_of_max, state.contacts.size),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contact cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.contacts.forEach { contact ->
                        EmergencyContactCard(
                            contact = contact,
                            onEditClick = {
                                viewModel.onAction(EmergencyContactsAction.OnEditContactClick(contact))
                            },
                            onDeleteClick = {
                                viewModel.onAction(EmergencyContactsAction.OnDeleteContactClick(contact))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AutoCall911Section(
                    isEnabled = state.autoCall911,
                    onToggle = { viewModel.onAction(EmergencyContactsAction.OnAutoCall911Toggle(it)) }
                )

                Spacer(modifier = Modifier.height(88.dp))
            }
        }
    }

    if (state.showAddContactSheet) {
        AddEmergencyContactBottomSheet(
            contactToEdit = state.contactToEdit,
            sheetState = sheetState,
            onDismiss = { viewModel.onAction(EmergencyContactsAction.OnDismissAddContactSheet) },
            onSave = { contact -> viewModel.onAction(EmergencyContactsAction.OnSaveContact(contact)) }
        )
    }

    if (state.showDeleteDialog && state.contactToDelete != null) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.emergency_delete_contact_title),
            description = stringResource(
                Res.string.emergency_delete_contact_desc,
                state.contactToDelete!!.name
            ),
            confirmButtonText = stringResource(Res.string.emergency_delete_contact_confirm),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { viewModel.onAction(EmergencyContactsAction.OnConfirmDeleteContact) },
            onCancelClick = { viewModel.onAction(EmergencyContactsAction.OnDismissDeleteDialog) },
            onDismiss = { viewModel.onAction(EmergencyContactsAction.OnDismissDeleteDialog) }
        )
    }

    if (state.showSosCountdown) {
        SosCountdownDialog(
            countdown = state.sosCountdown,
            onCancel = { viewModel.onAction(EmergencyContactsAction.OnSosCancel) }
        )
    }
}

@Composable
private fun EmergencyContactsEmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ContactPhone,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.emergency_contacts_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(Res.string.emergency_contacts_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        ChirpButton(
            text = stringResource(Res.string.emergency_contacts_add_first),
            onClick = onAddClick,
            style = AppButtonStyle.SECONDARY
        )
    }
}

@Composable
private fun EmergencyContactCard(
    contact: EmergencyContact,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (contact.relationship.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = contact.relationship,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AutoCall911Section(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.emergency_auto_call_911),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.emergency_auto_call_911_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}
