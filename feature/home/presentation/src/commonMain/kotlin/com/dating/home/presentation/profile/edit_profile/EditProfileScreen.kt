package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.contact_chirp_support_change_email
import aura.feature.home.presentation.generated.resources.current_password
import aura.feature.home.presentation.generated.resources.delete
import aura.feature.home.presentation.generated.resources.delete_profile_picture
import aura.feature.home.presentation.generated.resources.delete_profile_picture_desc
import aura.feature.home.presentation.generated.resources.email
import aura.feature.home.presentation.generated.resources.new_password
import aura.feature.home.presentation.generated.resources.password
import aura.feature.home.presentation.generated.resources.password_change_successful
import aura.feature.home.presentation.generated.resources.password_hint
import aura.feature.home.presentation.generated.resources.profile_image
import aura.feature.home.presentation.generated.resources.save
import aura.feature.home.presentation.generated.resources.upload_image
import com.dating.home.presentation.profile.components.ProfileSectionLayout
import com.dating.home.presentation.profile.mediapicker.rememberImagePickerLauncher
import com.dating.home.presentation.profile.profile.ProfileViewModel
import com.dating.home.presentation.profile.profile.ProfileAction
import com.dating.core.designsystem.components.avatar.AvatarSize
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.core.designsystem.components.brand.ChirpHorizontalDivider
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.textfields.ChirpPasswordTextField
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.clearFocusOnTap
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher = rememberImagePickerLauncher { pickedImageData ->
        viewModel.onAction(ProfileAction.OnPictureSelected(
            pickedImageData.bytes,
            pickedImageData.mimeType
        ))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .clearFocusOnTap()
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileSectionLayout(
                headerText = stringResource(Res.string.profile_image)
            ) {
                Row {
                    ChirpAvatarPhoto(
                        displayText = state.userInitials,
                        size = AvatarSize.LARGE,
                        imageUrl = state.profilePictureUrl,
                        onClick = {
                            launcher.launch()
                        }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ChirpButton(
                            text = stringResource(Res.string.upload_image),
                            onClick = {
                                launcher.launch()
                            },
                            style = AppButtonStyle.SECONDARY,
                            enabled = !state.isUploadingImage && !state.isDeletingImage,
                            isLoading = state.isUploadingImage,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = stringResource(Res.string.upload_image)
                                )
                            }
                        )
                        ChirpButton(
                            text = stringResource(Res.string.delete),
                            onClick = {
                                viewModel.onAction(ProfileAction.OnDeletePictureClick)
                            },
                            style = AppButtonStyle.DESTRUCTIVE_SECONDARY,
                            enabled = !state.isUploadingImage
                                    && !state.isDeletingImage
                                    && state.profilePictureUrl != null,
                            isLoading = state.isDeletingImage,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(Res.string.delete)
                                )
                            }
                        )
                    }
                }

                if (state.imageError != null) {
                    Text(
                        text = state.imageError?.asString() ?: "Error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            ChirpHorizontalDivider()
            ProfileSectionLayout(
                headerText = stringResource(Res.string.email)
            ) {
                ChirpTextField(
                    state = state.emailTextState,
                    enabled = false,
                    supportingText = stringResource(Res.string.contact_chirp_support_change_email)
                )
            }
            ChirpHorizontalDivider()
            ProfileSectionLayout(
                headerText = stringResource(Res.string.password)
            ) {
                ChirpPasswordTextField(
                    state = state.currentPasswordTextState,
                    isPasswordVisible = state.isCurrentPasswordVisible,
                    onToggleVisibilityClick = {
                        viewModel.onAction(ProfileAction.OnToggleCurrentPasswordVisibility)
                    },
                    placeholder = stringResource(Res.string.current_password),
                    isError = state.newPasswordError != null,
                )
                ChirpPasswordTextField(
                    state = state.newPasswordTextState,
                    isPasswordVisible = state.isNewPasswordVisible,
                    onToggleVisibilityClick = {
                        viewModel.onAction(ProfileAction.OnToggleNewPasswordVisibility)
                    },
                    placeholder = stringResource(Res.string.new_password),
                    isError = state.newPasswordError != null,
                    supportingText = state.newPasswordError?.asString()
                        ?: stringResource(Res.string.password_hint)
                )
                if (state.isPasswordChangeSuccessful) {
                    Text(
                        text = stringResource(Res.string.password_change_successful),
                        color = MaterialTheme.colorScheme.extended.success,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                ) {
                    ChirpButton(
                        text = stringResource(Res.string.save),
                        onClick = {
                            viewModel.onAction(ProfileAction.OnChangePasswordClick)
                        },
                        enabled = state.canChangePassword,
                        isLoading = state.isChangingPassword
                    )
                }
            }
        }

        if (state.showDeleteConfirmationDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.delete_profile_picture),
                description = stringResource(Res.string.delete_profile_picture_desc),
                confirmButtonText = stringResource(Res.string.delete),
                cancelButtonText = stringResource(Res.string.cancel),
                onConfirmClick = {
                    viewModel.onAction(ProfileAction.OnConfirmDeleteClick)
                },
                onCancelClick = {
                    viewModel.onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
                },
                onDismiss = {
                    viewModel.onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
                }
            )
        }
    }
}
