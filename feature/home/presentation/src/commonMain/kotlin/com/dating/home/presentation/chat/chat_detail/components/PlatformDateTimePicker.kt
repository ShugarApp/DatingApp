package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.runtime.Composable

data class DateTimePickerResult(
    val dateTime: String // ISO-8601 format: "2026-04-10T19:30:00"
)

@Composable
expect fun PlatformDatePicker(
    initialDate: String?, // ISO date: "2026-04-10"
    onDateSelected: (String) -> Unit, // returns ISO date
    onDismiss: () -> Unit
)

@Composable
expect fun PlatformTimePicker(
    initialTime: String?, // "19:30"
    onTimeSelected: (String) -> Unit, // returns "HH:mm"
    onDismiss: () -> Unit
)
