package com.dating.home.presentation.home.bottom_navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.bottom_nav_feed
import shugar.feature.home.presentation.generated.resources.bottom_nav_matches
import shugar.feature.home.presentation.generated.resources.bottom_nav_messages
import shugar.feature.home.presentation.generated.resources.bottom_nav_profile
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomNavigationBar(
    selectedSection: BottomNavSection,
    onSectionSelected: (BottomNavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BottomNavSection.entries.forEach { section ->
            NavigationBarItem(
                selected = selectedSection == section,
                onClick = { onSectionSelected(section) },
                icon = {
                    Icon(
                        imageVector = if (selectedSection == section) {
                            section.selectedIcon
                        } else {
                            section.unselectedIcon
                        },
                        contentDescription = bottomNavLabelText(section)
                    )
                },
                label = {
                    Text(
                        text = bottomNavLabelText(section),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun bottomNavLabelText(section: BottomNavSection): String {
    return when (section.labelKey) {
        BottomNavLabel.FEED -> stringResource(Res.string.bottom_nav_feed)
        BottomNavLabel.MATCHES -> stringResource(Res.string.bottom_nav_matches)
        BottomNavLabel.MESSAGES -> stringResource(Res.string.bottom_nav_messages)
        BottomNavLabel.PROFILE -> stringResource(Res.string.bottom_nav_profile)
    }
}
