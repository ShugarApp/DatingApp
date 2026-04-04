package com.dating.home.presentation.profile.settings

enum class DeleteAccountReason(val value: String) {
    FOUND_RELATIONSHIP("found_relationship"),
    TOO_MANY_MESSAGES("too_many_messages"),
    NO_COMPATIBLE_MATCHES("no_compatible_matches"),
    PRIVACY_CONCERNS("privacy_concerns"),
    TAKING_BREAK("taking_break"),
    POOR_APP_EXPERIENCE("poor_app_experience"),
    OTHER("other")
}
