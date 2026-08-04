package com.example.codebox.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codebox.presentation.create_item.CreateItemScreen
import com.example.codebox.presentation.detail.DetailScreen

import com.example.codebox.presentation.feed.FeedScreen
import com.example.codebox.presentation.feed.FeedUiState
import com.example.codebox.presentation.feed.FeedViewModel
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
                // rememberNavController - контроллер, через который переключаешь экраны
                val navController = rememberNavController()

                // NavHost - контейнер, который показывает один экран по времени
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
                        // getBackStackEntry - получает "запись" экрана "feed" из стека навигации
                        val parentEntry = remember { navController.getBackStackEntry("feed") }
                        // hiltViewModel(parentEntry) - говорит Hilt не создавать новый FeedViewModel а взять тот что уже привязан к экрану feed
                        val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)
                        CreateItemScreen(
                            onSaveItem = { newItem ->
                                feedViewModel.addItem(newItem)
                                // возврат на предыдущий экран
                                navController.popBackStack()
                            },
                            onCancel = { navController.popBackStack()}
                        )
                    }

                    composable("detail/{itemId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")
                        val parentEntry = remember { navController.getBackStackEntry("feed") }
                        val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)

                        val uiState by feedViewModel.uiState.collectAsStateWithLifecycle()
                        val item = (uiState as? FeedUiState.Success)?.items?.find { it.id == itemId }

                        if (item != null) {
                            DetailScreen(
                                item = item,
                                onBack = { navController.popBackStack() }
                            )
                        } else {
                            Text("item not found")
                        }
                    }
                }
            }
        }
    }
}