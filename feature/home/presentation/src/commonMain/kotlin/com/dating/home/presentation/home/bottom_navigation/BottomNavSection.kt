package com.dating.home.presentation.home.bottom_navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavLabel {
    FEED, MATCHES, MESSAGES, DATES, PROFILE
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
        selectedIcon = Icons.AutoMirrored.Filled.Send,
        unselectedIcon = Icons.AutoMirrored.Outlined.Send
    ),
    DATES(
        labelKey = BottomNavLabel.DATES,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    PROFILE(
        labelKey = BottomNavLabel.PROFILE,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
