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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
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
import com.example.codebox.presentation.likes.LikesListScreen
import com.example.codebox.presentation.notification.NotificationScreen
import com.example.codebox.presentation.profile.ProfileScreen
import com.example.codebox.presentation.profile.ProfileViewModel
import com.example.codebox.presentation.review_form.ReviewFormScreen
import com.example.codebox.presentation.settings.SettingsScreen
import com.example.codebox.presentation.theme.CodeboxTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

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
                        currentRoute?.startsWith("notification") == true -> "notification"
                        else -> "feed"
                    }

                    Scaffold(
                        bottomBar = {
                            val currentUserId = Firebase.auth.currentUser?.uid ?: ""
                            WireBottomBar(
                                currentTab = currentTab,
                                onFeed = {
                                    navController.navigate("feed")
                                },
                                onSearch = {
                                    navController.navigate("catalog/${CatalogMode.SEARCH.name}")
                                },
                                onNotification = {
                                    navController.navigate("notification")
                                },
                                onProfile = {
                                    navController.navigate("profile/$currentUserId")
                                },
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

                            composable(
                                route = "catalog/type/{type}",
                                arguments = listOf(navArgument("type") {
                                    type = NavType.StringType
                                })
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

                            composable(
                                route = "catalog/{mode}",
                                arguments = listOf(navArgument("mode") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val mode = backStackEntry.arguments?.getString("mode") ?: "SEARCH"
                                val isSelectionMode = mode == "FAVOURITE_SELECT"

                                CatalogScreen(
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onItemClick = { itemId ->
                                        if (isSelectionMode) {
                                            navController.previousBackStackEntry?.savedStateHandle?.set("selected_item", itemId)
                                            navController.popBackStack()
                                        } else {
                                            navController.navigate("detail/$itemId")
                                        }
                                    },
                                    onSearchByTypeClick = {
                                        navController.navigate("catalog/${CatalogMode.TYPES_LIST.name}")
                                    },
                                    onHighestTopClick = {
                                        navController.navigate("catalog/${CatalogMode.TOP_RATED.name}")
                                    },
                                    onLowestTopClick = {
                                        navController.navigate("catalog/${CatalogMode.LOWEST_RATED.name}")
                                    },
                                    onTypeClick = { type ->
                                        navController.navigate("catalog/type/${Uri.encode(type)}")
                                    },
                                    isSelectionMode = isSelectionMode
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
                                ReviewFormScreen(
                                    onBack = { navController.popBackStack() },
                                    onAuthorClick = { authorUserId ->
                                        navController.navigate("profile/$authorUserId")
                                    },
                                    onShowLikes = { itemId, reviewUserId ->
                                        navController.navigate("likes/$itemId/$reviewUserId")
                                    }
                                )
                            }

                            composable(
                                route = "profile/{userId}",
                                arguments = listOf(navArgument("userId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                })
                            ) { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId")
                                val profileViewModel: ProfileViewModel = hiltViewModel()

                                // Обработка результата выбора айтема
                                DisposableEffect(Unit) {
                                    val savedStateHandle = backStackEntry.savedStateHandle
                                    val observer = Observer<String> { itemId ->
                                        if (itemId != null) {
                                            profileViewModel.addFavouriteItem(itemId)
                                            savedStateHandle.remove<String>("selected_item")
                                        }
                                    }
                                    savedStateHandle.getLiveData<String>("selected_item").observeForever(observer)
                                    onDispose {
                                        savedStateHandle.getLiveData<String>("selected_item").removeObserver(observer)
                                    }
                                }

                                ProfileScreen(
                                    userId = userId,
                                    viewModel = profileViewModel,
                                    onReviewClick = { itemId ->
                                        navController.navigate("detail/$itemId")
                                    },
                                    onEditReviewClick = { itemId ->
                                        navController.navigate("review_form/$itemId")
                                    },
                                    onSettingsClick = {
                                        navController.navigate("settings")
                                    },
                                    onAddFavouriteClick = {
                                        navController.navigate("catalog/${CatalogMode.FAVOURITE_SELECT.name}")
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = "likes/{itemId}/{userId}",
                                arguments = listOf(
                                    navArgument("itemId") { type = NavType.StringType },
                                    navArgument("userId") { type = NavType.StringType }
                                )
                            ) {
                                LikesListScreen(
                                    onBack = { navController.popBackStack() },
                                    onUserClick = { clickedUserId ->
                                        navController.navigate("profile/$clickedUserId")
                                    }
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

                            composable("notification") {
                                NotificationScreen(
                                    onReviewClick = { reviewId ->
                                        val itemId = if (reviewId.contains("_")) {
                                            reviewId.substringAfter("_")
                                        } else {
                                            reviewId
                                        }
                                        navController.navigate("review_form/$itemId")
                                    },
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