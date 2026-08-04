package com.example.codebox.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codebox.presentation.theme.CodeboxTheme
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
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "feed"
                ) {
                    composable("feed") {
                        FeedScreen(
                            onCreateItem = {
                                navController.navigate("create")
                            }
                        )
                    }

                    composable("create") {
                        val parentEntry = remember { navController.getBackStackEntry("feed") }
                        val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)
                        CreateItemScreen(
                            onSaveItem = { newItem ->
                                feedViewModel.addItem(newItem)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}