package com.example.codebox.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.codebox.presentation.auth.LoginScreen
import com.example.codebox.presentation.details.DetailScreen
import com.example.codebox.presentation.feed.FeedScreen
import com.example.codebox.presentation.profile.ProfileScreen
import com.example.codebox.presentation.profile.WireBottomBar
import com.example.codebox.presentation.review_form.ReviewFormScreen
import com.example.codebox.presentation.settings.SettingsScreen
import com.example.codebox.presentation.theme.CodeboxTheme
import com.example.codebox.presentation.user_profile.UserProfileScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.padding  // ← добавь это

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedGetBackStackEntry")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeboxTheme {
                var isLoggedIn by remember {
                    mutableStateOf(Firebase.auth.currentUser != null)
                }

                if (!isLoggedIn) {
                    LoginScreen(onAuthSuccess = { isLoggedIn = true })
                } else {
                    val navController = rememberNavController()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStackEntry?.destination?.route

                    // Определяем активный таб по шаблону роута
                    val currentTab = when {
                        currentRoute?.startsWith("feed") == true -> "feed"
                        currentRoute?.startsWith("detail") == true -> "feed"
                        currentRoute?.startsWith("review_form") == true -> "feed"
                        currentRoute?.startsWith("profile") == true -> "profile"
                        currentRoute?.startsWith("settings") == true -> "profile"
                        currentRoute?.startsWith("user_profile") == true -> "profile"
                        else -> "feed"
                    }

                    Scaffold(
                        bottomBar = {
                            WireBottomBar(
                                currentTab = currentTab,
                                onFeed = {
                                    if (currentTab != "feed") {
                                        navController.navigate("feed") {
                                            popUpTo("feed") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onSearch = {
                                    // TODO: открыть экран поиска
                                },
                                onProfile = {
                                    if (currentTab != "profile") {
                                        navController.navigate("profile") {
                                            popUpTo("profile") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "feed",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("feed") {
                                FeedScreen(
                                    onItemClick = { itemId ->
                                        navController.navigate("detail/$itemId")
                                    },
                                    onProfileClick = { navController.navigate("profile") },
                                    onSearchClick = {}
                                )
                            }

                            composable("detail/{itemId}") { backStackEntry ->
                                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                                DetailScreen(
                                    itemId = itemId,
                                    onBack = { navController.popBackStack() },
                                    onRateClick = {
                                        navController.navigate("review_form/$itemId")
                                    },
                                    onReviewAuthorClick = { authorUserId ->
                                        val currentUserId = Firebase.auth.currentUser?.uid
                                        if (authorUserId == currentUserId) {
                                            navController.navigate("profile")
                                        } else {
                                            navController.navigate("user_profile/$authorUserId")
                                        }
                                    },
                                    onReviewClick = { _, authorUserId ->
                                        navController.navigate("review_form/$itemId?userId=$authorUserId")
                                    }
                                )
                            }

                            composable(
                                route = "review_form/{itemId}?userId={userId}",
                                arguments = listOf(
                                    navArgument("itemId") { type = NavType.StringType },
                                    navArgument("userId") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    }
                                )
                            ) { backStackEntry ->
                                val itemId = backStackEntry.arguments?.getString("itemId")
                                    ?: return@composable
                                ReviewFormScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("profile") {
                                ProfileScreen(
                                    onReviewClick = { itemId ->
                                        navController.navigate("detail/$itemId")
                                    },
                                    onEditReviewClick = { itemId ->
                                        navController.navigate("review_form/$itemId")
                                    },
                                    onSettingsClick = {
                                        navController.navigate("settings")
                                    },
                                    onFeedClick = { navController.navigate("feed") },
                                    onSearchClick = {}
                                )
                            }

                            composable("settings") {
                                SettingsScreen(
                                    onBack = { navController.popBackStack() },
                                    onSignedOut = {
                                        Firebase.auth.signOut()
                                        isLoggedIn = false
                                    }
                                )
                            }

                            composable("user_profile/{userId}") { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId")
                                    ?: return@composable
                                UserProfileScreen(
                                    userId = userId,
                                    onBack = { navController.popBackStack() },
                                    onItemClick = { itemId ->
                                        navController.navigate("detail/$itemId")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}