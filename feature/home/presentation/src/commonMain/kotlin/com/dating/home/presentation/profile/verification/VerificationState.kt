package com.dating.home.presentation.profile.verification

import com.dating.core.domain.auth.ProfileVerification
import com.dating.core.domain.auth.VerificationStatus

enum class VerificationStep {
    STATUS,     // Initial screen showing current status
    GUIDANCE,   // Oval face guide + instructions before capturing
    PROCESSING, // Uploading + waiting for backend result
    RESULT      // Final result screen (VERIFIED or REJECTED)
}

data class VerificationState(
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val verification: ProfileVerification? = null,
    val step: VerificationStep = VerificationStep.STATUS,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null
)
