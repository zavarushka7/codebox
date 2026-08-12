package com.example.codebox.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codebox.presentation.auth.LoginScreen
import com.example.codebox.presentation.details.DetailScreen
import com.example.codebox.presentation.details.ReviewFormScreen
import com.example.codebox.presentation.feed.FeedScreen
import com.example.codebox.presentation.feed.FeedViewModel
import com.example.codebox.presentation.profile.ProfileScreen
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

                    NavHost(
                        navController = navController,
                        startDestination = "feed"
                    ) {
                        composable("feed") {
                            FeedScreen(

                                onItemClick = { itemId ->
                                    navController.navigate("detail/$itemId")
                                },
                                onProfileClick = { navController.navigate("profile")},
                                onSearchClick = {}
                            )
                        }

                        composable("detail/{itemId}") { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                            DetailScreen(
                                itemId = itemId,
                                onBack = { navController.popBackStack() },
                                onRateClick = { navController.navigate("review_form/$itemId") }
                            )
                        }
                        composable("review_form/{itemId}") { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                            ReviewFormScreen(
                                itemId = itemId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(


                                onReviewClick = { itemId ->
                                    navController.navigate("detail/$itemId")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                },
                                onFeedClick = { navController.navigate("feed")},
                                onSearchClick = {}

                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onSignedOut = { isLoggedIn = false },
                            )
                        }
                    }
                }
            }
        }
    }
}