package com.zello.sdk.example.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.zello.sdk.example.app.screens.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    var selectedNavIndex by remember { mutableIntStateOf(0)}
    val navItems = listOf(
        NavItem(
            title = "Channels",
            icon = Icons.Default.Phone,
            route = Screen.Channels.rout
        ),
        NavItem(
            title = "Recents",
            icon = Icons.Default.Warning,
            route = Screen.Recents.rout
        )
    )

    NavigationBar (
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavIndex == index,
                onClick = {
                    selectedNavIndex = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selectedNavIndex==index) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onBackground)
                },
                label = {
                    Text(
                        item.title,
                        color = if (index == selectedNavIndex)
                            MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

data class NavItem(
    val title : String,
    val icon : ImageVector,
    val route : String
)