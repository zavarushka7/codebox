package com.example.codebox.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
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
import com.example.codebox.presentation.catalog.CatalogMode
import com.example.codebox.presentation.catalog.CatalogScreen
import com.example.codebox.presentation.common.WireBottomBar
import com.example.codebox.presentation.details.DetailScreen
import com.example.codebox.presentation.feed.FeedScreen
import com.example.codebox.presentation.profile.ProfileScreen

import com.example.codebox.presentation.review_form.ReviewFormScreen
import com.example.codebox.presentation.settings.SettingsScreen
import com.example.codebox.presentation.theme.CodeboxTheme

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

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

                    val currentTab = when {
                        currentRoute?.startsWith("feed") == true -> "feed"
                        currentRoute?.startsWith("detail") == true -> "feed"
                        currentRoute?.startsWith("review_form") == true -> "feed"
                        currentRoute?.startsWith("profile") == true -> "profile"
                        currentRoute?.startsWith("settings") == true -> "profile"
                        currentRoute?.startsWith("catalog") == true -> "search"
                        else -> "feed"
                    }

                    Scaffold(
                        bottomBar = {
                            val currentUserId = Firebase.auth.currentUser?.uid ?: ""
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
                                    navController.navigate("catalog/${CatalogMode.SEARCH.name}")
                                },
                                onProfile = {
                                    if (currentTab != "profile") {
                                        navController.navigate("profile/$currentUserId") {
                                            popUpTo("feed") { inclusive = true }
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
                                    onSearchClick = {
                                        navController.navigate("catalog/${CatalogMode.SEARCH.name}")
                                    }
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

                                        navController.navigate("profile/$authorUserId")

                                    },
                                    onReviewClick = { _, authorUserId ->
                                        navController.navigate("review_form/$itemId?userId=$authorUserId")
                                    }
                                )
                            }


// 2. Айтемы по типу
                            composable(
                                route = "catalog/type/{type}",
                                arguments = listOf(navArgument("type") { type = NavType.StringType })
                            ) {
                                CatalogScreen(
                                    onBack = { navController.popBackStack() },
                                    onItemClick = { itemId -> navController.navigate("detail/$itemId") },
                                    onSearchByTypeClick = {},
                                    onTypeClick = {},
                                    onLowestTopClick = {},
                                    onHighestTopClick = {}
                                )
                            }

// 3. Остальные режимы
                            composable(
                                route = "catalog/{mode}",
                                arguments = listOf(navArgument("mode") { type = NavType.StringType })
                            ) {
                                CatalogScreen(
                                    onBack = { navController.popBackStack() },
                                    onItemClick = { itemId -> navController.navigate("detail/$itemId") },
                                    onSearchByTypeClick = {
                                        navController.navigate("catalog/${CatalogMode.TYPES_LIST.name}")
                                    },
                                    onTypeClick = { type ->
                                        navController.navigate("catalog/type/${Uri.encode(type)}")
                                    },
                                    onHighestTopClick = {
                                        navController.navigate("catalog/${CatalogMode.TOP_RATED.name}")
                                    },
                                    onLowestTopClick = {
                                        navController.navigate("catalog/${CatalogMode.LOWEST_RATED.name}")
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
//                                val itemId = backStackEntry.arguments?.getString("itemId")
//                                    ?: return@composable

                                ReviewFormScreen(
                                    onBack = { navController.popBackStack() },
                                    onAuthorClick = {  authorUserId ->
                                        navController.navigate("profile/$authorUserId")},

                                )
                            }

                            composable(
                                route = "profile/{userId}",
                                arguments = listOf(navArgument("userId"){
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            ) ){
                                backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId")
                                ProfileScreen(
                                    userId = userId,
                                    onReviewClick = { itemId ->
                                        navController.navigate("detail/$itemId")
                                    },
                                    onEditReviewClick = { itemId ->
                                        navController.navigate("review_form/$itemId")
                                    },
                                    onSettingsClick = {
                                        navController.navigate("settings")
                                    },
                                    onBack = { navController.popBackStack()}
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


                        }
                    }
                }
            }
        }
    }
}