package com.dating.home.domain.emergency

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String = ""
)
