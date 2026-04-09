package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.domain.auth.VerificationStatus

// ──────────────────────────────────────────────────────────
// SOS Button
// ──────────────────────────────────────────────────────────

@PreviewLightDark
@Composable
private fun SosButtonPreview() {
    AppTheme {
        SosActionButton(
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ──────────────────────────────────────────────────────────
// Dashboard Cards
// ──────────────────────────────────────────────────────────

@PreviewLightDark
@Composable
private fun DashboardCardsPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                ProfileDashboardCard(
                    icon = Icons.Default.Edit,
                    text = "Edit Profile",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                ProfileDashboardCard(
                    icon = Icons.Default.Security,
                    text = "Safety Center",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                ProfileDashboardCard(
                    icon = Icons.Default.Settings,
                    text = "Settings",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// Verification Status Card — all states
// ──────────────────────────────────────────────────────────

@PreviewLightDark
@Composable
private fun VerificationCardVerifiedPreview() {
    AppTheme {
        VerificationStatusCard(
            status = VerificationStatus.VERIFIED,
            onVerifyClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Verification — Pending")
@Composable
private fun VerificationCardPendingPreview() {
    AppTheme {
        VerificationStatusCard(
            status = VerificationStatus.PENDING,
            onVerifyClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Verification — Rejected")
@Composable
private fun VerificationCardRejectedPreview() {
    AppTheme {
        VerificationStatusCard(
            status = VerificationStatus.REJECTED,
            onVerifyClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Verification — Unverified")
@Composable
private fun VerificationCardUnverifiedPreview() {
    AppTheme {
        VerificationStatusCard(
            status = VerificationStatus.UNVERIFIED,
            onVerifyClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ──────────────────────────────────────────────────────────
// Profile Completion Card
// ──────────────────────────────────────────────────────────

@Preview(name = "Completion — 40%")
@Composable
private fun ProfileCompletionCardLowPreview() {
    AppTheme {
        ProfileCompletionCard(
            completion = 40,
            onCompleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Completion — 80%")
@Composable
private fun ProfileCompletionCardHighPreview() {
    AppTheme {
        ProfileCompletionCard(
            completion = 80,
            onCompleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ──────────────────────────────────────────────────────────
// Full Profile Action Section — all possible states
// ──────────────────────────────────────────────────────────

@Preview(name = "Profile Section — Unverified + Incomplete + SOS", showBackground = true)
@Composable
private fun ProfileSectionUnverifiedWithSosPreview() {
    AppTheme {
        ProfileActionSection(
            verificationStatus = VerificationStatus.UNVERIFIED,
            profileCompletion = 50,
            showSosButton = true,
        )
    }
}

@Preview(name = "Profile Section — Unverified + Incomplete, no SOS", showBackground = true)
@Composable
private fun ProfileSectionUnverifiedNoSosPreview() {
    AppTheme {
        ProfileActionSection(
            verificationStatus = VerificationStatus.UNVERIFIED,
            profileCompletion = 50,
            showSosButton = false,
        )
    }
}

@Preview(name = "Profile Section — Pending + Incomplete", showBackground = true)
@Composable
private fun ProfileSectionPendingPreview() {
    AppTheme {
        ProfileActionSection(
            verificationStatus = VerificationStatus.PENDING,
            profileCompletion = 70,
            showSosButton = true,
        )
    }
}

@Preview(name = "Profile Section — Rejected", showBackground = true)
@Composable
private fun ProfileSectionRejectedPreview() {
    AppTheme {
        ProfileActionSection(
            verificationStatus = VerificationStatus.REJECTED,
            profileCompletion = 60,
            showSosButton = true,
        )
    }
}

@Preview(name = "Profile Section — Verified + Complete", showBackground = true)
@Composable
private fun ProfileSectionVerifiedCompletePreview() {
    AppTheme {
        ProfileActionSection(
            verificationStatus = VerificationStatus.VERIFIED,
            profileCompletion = 100,
            showSosButton = true,
        )
    }
}

@Preview(name = "Profile Section — Verified + Incomplete", showBackground = true)
@Composable
private fun ProfileSectionVerifiedIncompletePreview() {
    AppTheme {
        ProfileActionSection(
            verificationStatus = VerificationStatus.VERIFIED,
            profileCompletion = 80,
            showSosButton = false,
        )
    }
}

/**
 * Reusable preview helper that renders the action section
 * (dashboard cards + SOS + verification banner + completion card).
 */
@Composable
private fun ProfileActionSection(
    verificationStatus: VerificationStatus,
    profileCompletion: Int,
    showSosButton: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Verification banner — above action buttons
        VerificationStatusCard(
            status = verificationStatus,
            onVerifyClick = {}
        )

        // Profile completion card (only when < 100)
        if (profileCompletion < 100) {
            Spacer(modifier = Modifier.height(12.dp))
            ProfileCompletionCard(
                completion = profileCompletion,
                onCompleteClick = {}
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dashboard cards row — always 3 fixed cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            ProfileDashboardCard(
                icon = Icons.Default.Edit,
                text = "Edit Profile",
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            ProfileDashboardCard(
                icon = Icons.Default.Security,
                text = "Safety Center",
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            ProfileDashboardCard(
                icon = Icons.Default.Settings,
                text = "Settings",
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Safety section placeholder
        androidx.compose.material3.Text(
            text = "SAFETY",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // SOS Button — below Safety section
        if (showSosButton) {
            Spacer(modifier = Modifier.height(12.dp))
            SosActionButton(onClick = {})
        }
    }
}
