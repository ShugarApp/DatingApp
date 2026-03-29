package com.dating.home.presentation.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.report_description_counter
import aura.feature.home.presentation.generated.resources.report_description_placeholder
import aura.feature.home.presentation.generated.resources.report_reason_fake_profile
import aura.feature.home.presentation.generated.resources.report_reason_harassment
import aura.feature.home.presentation.generated.resources.report_reason_inappropriate_content
import aura.feature.home.presentation.generated.resources.report_reason_other
import aura.feature.home.presentation.generated.resources.report_reason_scam
import aura.feature.home.presentation.generated.resources.report_reason_spam
import aura.feature.home.presentation.generated.resources.report_reason_underage
import aura.feature.home.presentation.generated.resources.report_submit
import aura.feature.home.presentation.generated.resources.report_submitting
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
    ReportReasonOption(ReportReason.SCAM, Res.string.report_reason_scam),
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
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.report_user_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(Res.string.report_user_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        reportReasonOptions.forEach { option ->
            val isSelected = selectedReason == option.reason
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedReason = option.reason }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(option.labelRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 1000) description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(Res.string.report_description_placeholder))
            },
            supportingText = {
                Text(stringResource(Res.string.report_description_counter, description.length))
            },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                selectedReason?.let { reason ->
                    onSubmit(reason, description.takeIf { it.isNotBlank() })
                }
            },
            enabled = selectedReason != null && !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(Res.string.report_submit))
            }
        }
    }
}
