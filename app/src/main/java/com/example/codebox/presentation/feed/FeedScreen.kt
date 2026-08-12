package com.example.codebox.presentation.feed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)

@Composable
fun FeedScreen(
    onItemClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeedScreenContent(
        uiState = uiState,
        onItemClick = onItemClick,
        onSearchClick = onSearchClick,
        onProfileClick = onProfileClick
    )
}

@Composable
fun FeedScreenContent(
    uiState: FeedUiState,
    onItemClick: (String) -> Unit,
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // ── Шапка ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 12.dp)
            ) {
                    Text(
                        text = "// codebox",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,

                    )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalLine(strokeWidth = Hairline)
            }

            // ── Контент (занимает всё место между шапкой и панелью) ──
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is FeedUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            BlinkingCursor()
                        }
                    }

                    is FeedUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.message, color = TextPrimary)
                        }
                    }

                    is FeedUiState.Success -> {
                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("// no items", color = TextSecondary)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(state.items, key = { it.id }) { item ->
                                    ItemWireRow(
                                        item = item,
                                        onClick = { onItemClick(item.id) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }

            // ── Нижняя панель (прижата к низу) ──
            WireBottomBar(
                currentTab = "feed",
                onFeed = {},
                onSearch = onSearchClick,
                onProfile = onProfileClick
            )
        }
    }
}

@Composable
fun WireBottomBar(
    currentTab: String,
    onFeed: () -> Unit,
    onSearch: () -> Unit,
    onProfile: () -> Unit
) {
    Column {
        HorizontalLine()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(PureBlack),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavIcon(icon = Icons.Outlined.Home,
                label = "feed",

                selected = currentTab == "feed",
                onClick = onFeed)
            BottomNavIcon(icon = Icons.Outlined.Search,
                label = "search",
                selected = currentTab == "search",
                onClick = onSearch)
            BottomNavIcon(icon = Icons.Outlined.Person,
                label = "profile",
                selected = currentTab == "profile",
                onClick = onProfile)

        }
    }
}
@Composable
private fun BottomNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) TerminalGreen else TextSecondary,
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = onClick)
    )
}
@Composable
fun ItemWireRow(item: Item, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = LineColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = Wire)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.lowercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}



@Composable
fun HorizontalLine(
    modifier: Modifier = Modifier,
    color: Color = LineColor,
    strokeWidth: Float = Wire
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun BlinkingCursor() {
    Text(
        text = "█",
        color = TerminalGreen,
        style = MaterialTheme.typography.displayLarge
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5",
    backgroundColor = 0xFF000000
)
@Composable
fun FeedScreenPreview() {
    CodeboxTheme {
        FeedScreenContent(
            uiState = FeedUiState.Success(
                listOf(
                    Item(name = "kotlin", description = "jvm language"),
                    Item(name = "rust", description = "systems language"),
                    Item(name = "go", description = "")
                )
            ),

            onItemClick = {},
            onProfileClick = {},
            onSearchClick = {}
        )
    }
}