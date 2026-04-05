package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformDatePicker(
    initialDate: String?,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select date",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            UIKitView(
                factory = {
                    UIDatePicker().apply {
                        datePickerMode = UIDatePickerMode.UIDatePickerModeDate
                        preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels

                        // Set initial date if provided
                        if (initialDate != null) {
                            val formatter = NSDateFormatter().apply {
                                dateFormat = "yyyy-MM-dd"
                            }
                            formatter.dateFromString(initialDate)?.let { date = it }
                        }
                    }
                },
                update = { datePicker ->
                    val formatter = NSDateFormatter().apply {
                        dateFormat = "yyyy-MM-dd"
                    }
                    selectedDate = formatter.stringFromDate(datePicker.date)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = {
                        if (selectedDate.isNotEmpty()) {
                            onDateSelected(selectedDate)
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformTimePicker(
    initialTime: String?,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime ?: "12:00") }

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

            UIKitView(
                factory = {
                    UIDatePicker().apply {
                        datePickerMode = UIDatePickerMode.UIDatePickerModeTime
                        preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels

                        // Set initial time
                        if (initialTime != null) {
                            val parts = initialTime.split(":")
                            if (parts.size == 2) {
                                val calendar = NSCalendar.currentCalendar
                                val components = NSDateComponents().apply {
                                    hour = parts[0].toLongOrNull() ?: 12
                                    minute = parts[1].toLongOrNull() ?: 0
                                }
                                calendar.dateFromComponents(components)?.let { date = it }
                            }
                        }
                    }
                },
                update = { timePicker ->
                    val formatter = NSDateFormatter().apply {
                        dateFormat = "HH:mm"
                    }
                    selectedTime = formatter.stringFromDate(timePicker.date)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = {
                        onTimeSelected(selectedTime)
                    }
                ) {
                    Text("OK")
                }
            }
        }
    }
}
