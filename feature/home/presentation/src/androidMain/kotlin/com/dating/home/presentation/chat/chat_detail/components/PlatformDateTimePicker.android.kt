package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    initialDate: String?,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate?.let {
        try {
            LocalDate.parse(it).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        } catch (_: Exception) { null }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC)
                            .date
                        onDateSelected(localDate.toString())
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformTimePicker(
    initialTime: String?,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (initialHour, initialMinute) = initialTime?.let {
        try {
            val parts = it.split(":")
            parts[0].toInt() to parts[1].toInt()
        } catch (_: Exception) { 12 to 0 }
    } ?: (12 to 0)

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select time",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TimePicker(state = timePickerState)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = {
                        val hour = timePickerState.hour.toString().padStart(2, '0')
                        val minute = timePickerState.minute.toString().padStart(2, '0')
                        onTimeSelected("$hour:$minute")
                    }
                ) {
                    Text("OK")
                }
            }
        }
    }
}
