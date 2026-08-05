package com.arxivai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arxivai.ui.bookmarks.BookmarksScreen
import com.arxivai.ui.detail.DetailScreen
import com.arxivai.ui.feed.FeedScreen
import com.arxivai.ui.history.HistoryScreen
import com.arxivai.ui.search.SearchScreen
import com.arxivai.ui.settings.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Feed : Screen("feed", "Papers", Icons.Outlined.Home, Icons.Filled.Home)
    data object Search : Screen("search", "Search", Icons.Outlined.Search, Icons.Filled.Search)
    data object Bookmarks : Screen("bookmarks", "Bookmarks", Icons.Outlined.BookmarkBorder, Icons.Filled.Bookmark)
    data object History : Screen("history", "History", Icons.Outlined.History, Icons.Filled.History)
    data object Settings : Screen("settings", "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
    data object Detail : Screen("detail/{paperId}", "Detail", Icons.Outlined.Article, Icons.Filled.Article) {
        fun createRoute(paperId: String) = "detail/$paperId"
    }
}

val bottomNavItems = listOf(Screen.Feed, Screen.Search, Screen.Bookmarks, Screen.History)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArXivNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if we should show the bottom bar
    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = NavigationBarDefaults.Elevation
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 4 }) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it / 4 }) }
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(onPaperClick = { paperId ->
                    navController.navigate(Screen.Detail.createRoute(paperId))
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(onPaperClick = { paperId ->
                    navController.navigate(Screen.Detail.createRoute(paperId))
                })
            }
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(onPaperClick = { paperId ->
                    navController.navigate(Screen.Detail.createRoute(paperId))
                })
            }
            composable(Screen.History.route) {
                HistoryScreen(onPaperClick = { paperId ->
                    navController.navigate(Screen.Detail.createRoute(paperId))
                })
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("paperId") { type = NavType.StringType })
            ) { backStackEntry ->
                val paperId = backStackEntry.arguments?.getString("paperId") ?: return@composable
                DetailScreen(
                    paperId = paperId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}