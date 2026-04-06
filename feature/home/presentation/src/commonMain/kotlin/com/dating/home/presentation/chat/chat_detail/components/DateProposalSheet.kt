package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateProposalSheet(
    onDismiss: () -> Unit,
    onSubmit: (dateTime: String, location: String) -> Unit,
    initialDateTime: String? = null,
    initialLocation: String? = null,
    isEditing: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Parse initial date/time
    val (initDate, initTime) = remember(initialDateTime) {
        if (initialDateTime != null) {
            val parts = initialDateTime.split("T")
            if (parts.size == 2) {
                parts[0] to parts[1].take(5)
            } else {
                "" to ""
            }
        } else {
            "" to ""
        }
    }

    var selectedDate by remember { mutableStateOf(initDate) }
    var selectedTime by remember { mutableStateOf(initTime) }
    var location by remember { mutableStateOf(initialLocation ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val isFormValid = selectedDate.isNotBlank() && selectedTime.isNotBlank() && location.isNotBlank()

    if (showDatePicker) {
        PlatformDatePicker(
            initialDate = selectedDate.ifBlank { null },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        PlatformTimePicker(
            initialTime = selectedTime.ifBlank { null },
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditing) "Edit Date Proposal" else "Propose a Date",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Date selector
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (selectedDate.isNotBlank()) selectedDate else "Select date",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedDate.isNotBlank()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Time selector
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (selectedTime.isNotBlank()) selectedTime else "Select time",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedTime.isNotBlank()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Location input
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("e.g. Central Park, Coffee Shop...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Submit button
            Button(
                onClick = {
                    val dateTime = "${selectedDate}T${selectedTime}:00"
                    onSubmit(dateTime, location)
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEditing) "Update Proposal" else "Send Proposal",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
