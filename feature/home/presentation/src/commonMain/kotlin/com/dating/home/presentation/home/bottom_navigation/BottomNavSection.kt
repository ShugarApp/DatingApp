package com.dating.home.presentation.home.bottom_navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavLabel {
    FEED, MATCHES, MESSAGES, PROFILE
}

enum class BottomNavSection(
    val labelKey: BottomNavLabel,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    FEED(
        labelKey = BottomNavLabel.FEED,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    MATCHES(
        labelKey = BottomNavLabel.MATCHES,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    MESSAGES(
        labelKey = BottomNavLabel.MESSAGES,
        selectedIcon = Icons.Filled.Send,
        unselectedIcon = Icons.Outlined.Send
    ),
    PROFILE(
        labelKey = BottomNavLabel.PROFILE,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
