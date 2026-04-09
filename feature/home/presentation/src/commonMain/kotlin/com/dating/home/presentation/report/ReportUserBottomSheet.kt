package com.dating.home.presentation.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.report_description_counter
import aura.feature.home.presentation.generated.resources.report_description_placeholder
import aura.feature.home.presentation.generated.resources.report_reason_fake_profile
import aura.feature.home.presentation.generated.resources.report_reason_harassment
import aura.feature.home.presentation.generated.resources.report_reason_inappropriate_content
import aura.feature.home.presentation.generated.resources.report_reason_other
import aura.feature.home.presentation.generated.resources.report_reason_spam
import aura.feature.home.presentation.generated.resources.report_reason_underage
import aura.feature.home.presentation.generated.resources.report_submit
import aura.feature.home.presentation.generated.resources.report_user_subtitle
import aura.feature.home.presentation.generated.resources.report_user_title
import com.dating.core.designsystem.theme.extended
import com.dating.home.domain.report.ReportReason
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class ReportReasonOption(
    val reason: ReportReason,
    val labelRes: StringResource
)

private val reportReasonOptions = listOf(
    ReportReasonOption(ReportReason.HARASSMENT, Res.string.report_reason_harassment),
    ReportReasonOption(ReportReason.FAKE_PROFILE, Res.string.report_reason_fake_profile),
    ReportReasonOption(ReportReason.INAPPROPRIATE_CONTENT, Res.string.report_reason_inappropriate_content),
    ReportReasonOption(ReportReason.UNDERAGE, Res.string.report_reason_underage),
    ReportReasonOption(ReportReason.SPAM, Res.string.report_reason_spam),
    ReportReasonOption(ReportReason.OTHER, Res.string.report_reason_other),
)

@Composable
fun ReportUserBottomSheet(
    isSubmitting: Boolean,
    onSubmit: (ReportReason, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Header ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Text(
                text = stringResource(Res.string.report_user_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.report_user_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )

        // ── Reason selection ────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            reportReasonOptions.forEach { option ->
                val isSelected = selectedReason == option.reason
                Surface(
                    onClick = { selectedReason = option.reason },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(option.labelRes),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Optional description ────────────────────────────────────────
        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 1000) description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(Res.string.report_description_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.extended.textSecondary
                )
            },
            supportingText = {
                Text(
                    text = stringResource(Res.string.report_description_counter, description.length),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary
                )
            },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
            )
        )

        // ── Submit ──────────────────────────────────────────────────────
        Button(
            onClick = {
                selectedReason?.let { reason ->
                    onSubmit(reason, description.takeIf { it.isNotBlank() })
                }
            },
            enabled = selectedReason != null && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(Res.string.report_submit),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
