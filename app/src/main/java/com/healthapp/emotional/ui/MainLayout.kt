package com.healthapp.emotional.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.healthapp.emotional.data.ChatRepository
import com.healthapp.emotional.navigation.NavigationItem
import com.healthapp.emotional.ui.screens.*
import com.healthapp.emotional.ui.viewmodels.SessionsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    chatRepository: ChatRepository,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                NavigationItem.drawerNavigationItems().forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(item.route)
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("MoodCare") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationItem.bottomNavigationItems().forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = { navController.navigate(item.route) }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = NavigationItem.Home.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(NavigationItem.Home.route) {
                    HomeScreen()
                }
                composable(NavigationItem.Sessions.route) {
                    val sessionViewModel: SessionsViewModel = hiltViewModel()
                    SessionsScreen(sessionViewModel = sessionViewModel)
                }
                composable(NavigationItem.Content.route) {
                    ContentScreen()
                }
                composable(NavigationItem.Chat.route) {
                    ChatScreen(chatRepository = chatRepository)
                }
                composable(NavigationItem.Explore.route) {
                    ExploreScreen()
                }
                composable(NavigationItem.Profile.route) {
                    ProfileScreen(
                        onNavigateToHome = {
                            navController.navigate(NavigationItem.Home.route) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(NavigationItem.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
} 