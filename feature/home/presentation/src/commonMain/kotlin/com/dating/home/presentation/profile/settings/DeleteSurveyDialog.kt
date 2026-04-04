package com.dating.home.presentation.profile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete_reason_found_relationship
import aura.feature.home.presentation.generated.resources.delete_reason_no_matches
import aura.feature.home.presentation.generated.resources.delete_reason_other
import aura.feature.home.presentation.generated.resources.delete_reason_poor_experience
import aura.feature.home.presentation.generated.resources.delete_reason_privacy
import aura.feature.home.presentation.generated.resources.delete_reason_taking_break
import aura.feature.home.presentation.generated.resources.delete_reason_too_many_messages
import aura.feature.home.presentation.generated.resources.delete_survey_continue
import aura.feature.home.presentation.generated.resources.delete_survey_subtitle
import aura.feature.home.presentation.generated.resources.delete_survey_title
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteSurveyDialog(
    selectedReason: DeleteAccountReason?,
    onReasonSelected: (DeleteAccountReason) -> Unit,
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.delete_survey_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.delete_survey_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    surveyOptions.forEach { (reason, labelRes) ->
                        SurveyOptionRow(
                            label = stringResource(labelRes),
                            selected = selectedReason == reason,
                            onClick = { onReasonSelected(reason) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                ChirpButton(
                    text = stringResource(Res.string.delete_survey_continue),
                    onClick = onContinue,
                    style = AppButtonStyle.PRIMARY,
                    enabled = selectedReason != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.cancel),
                        color = MaterialTheme.colorScheme.extended.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SurveyOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textPrimary
        )
    }
}

private val surveyOptions: List<Pair<DeleteAccountReason, StringResource>> = listOf(
    DeleteAccountReason.FOUND_RELATIONSHIP to Res.string.delete_reason_found_relationship,
    DeleteAccountReason.TOO_MANY_MESSAGES to Res.string.delete_reason_too_many_messages,
    DeleteAccountReason.NO_COMPATIBLE_MATCHES to Res.string.delete_reason_no_matches,
    DeleteAccountReason.PRIVACY_CONCERNS to Res.string.delete_reason_privacy,
    DeleteAccountReason.TAKING_BREAK to Res.string.delete_reason_taking_break,
    DeleteAccountReason.POOR_APP_EXPERIENCE to Res.string.delete_reason_poor_experience,
    DeleteAccountReason.OTHER to Res.string.delete_reason_other
)
