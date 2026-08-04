package com.example.codebox.presentation.feed

import android.R.style
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.presentation.theme.CodeboxTheme
import com.example.codebox.presentation.theme.GoldStar
import com.example.codebox.presentation.theme.TerminalGreen
import com.example.codebox.presentation.theme.TerminalGreenBorder
import com.example.codebox.presentation.theme.TerminalGreenDark
import com.example.codebox.presentation.theme.TextMuted
import com.example.codebox.presentation.theme.TextSecondary


/* ═══════════════════════════════════════
   Stateful wrapper — используется в приложении
   ═══════════════════════════════════════ */
@Composable
fun FeedScreen(
    onCreateItem: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeedScreenContent(
        uiState = uiState,
        onCreateItem = onCreateItem
    )
}

/* ═══════════════════════════════════════
   Stateless content — сюда приходят данные,
   отсюда рисуется всё на экране
   ═══════════════════════════════════════ */
@Composable
fun FeedScreenContent(
    uiState: FeedUiState,
    onCreateItem: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            Button(
                onClick = onCreateItem,
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerminalGreenDark,
                    contentColor = TerminalGreen
                ),
                border = BorderStroke(1.dp, TerminalGreenBorder)
            ) {
                Text("[+]")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is FeedUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TerminalGreen)
                    }
                }

                is FeedUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is FeedUiState.Success -> {
                    if (state.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "// no items",
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.items, key = { it.id }) { item ->
                                ItemCard(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: Item) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(0.dp),
        colors = androidx.compose.material3.CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.name.lowercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (item.description?.isNotBlank() == true){
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary

                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "★".repeat(item.rating.coerceIn(0, 5)),
                    color = GoldStar
                )
                Text(
                    text = "★".repeat((5 - item.rating).coerceIn(0, 5)),
                    color = TextMuted
                )
                Text(
                    text = "  ${item.rating}/5",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

/* ═══════════════════════════════════════
   ОДНО PREVIEW для всего экрана
   ═══════════════════════════════════════ */
@Preview(
    showBackground = true,
    showSystemUi = true,           // показывает статус-бар и системные панели
    device = "id:pixel_5"          // размер как на Pixel 5
)
@Composable
fun FeedScreenFullPreview() {
    CodeboxTheme {
        FeedScreenContent(
            uiState = FeedUiState.Success(
                listOf(
                    Item(name = "kotlin", rating = 5, description = "jvm"),
                    Item(name = "python", rating = 4, description = "dynamic"),
                    Item(name = "rust", rating = 5, description = "systems"),
                    Item(name = "javascript", rating = 3, description = "web"),
                    Item(name = "docker", rating = 4, description = "containers")
                )
            ),
            onCreateItem = {}
        )
    }
}