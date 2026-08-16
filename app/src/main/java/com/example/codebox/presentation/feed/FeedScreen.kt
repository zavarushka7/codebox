package com.example.codebox.presentation.feed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.domain.TextCaseStyle
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val LineColor = Color(0xFF777777)

@Composable
fun FeedScreen(
    onItemClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()

    FeedScreenContent(
        caseStyle = caseStyle,
        uiState = uiState,
        onItemClick = onItemClick,
    )
}

@Composable
fun FeedScreenContent(
    caseStyle: TextCaseStyle,
    uiState: FeedUiState,
    onItemClick: (String) -> Unit,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "// codebox",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalLine(strokeWidth = Hairline)
            }

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
                                Text(
                                    text = "// no items".toDisplayCase(caseStyle),
                                    color = TextSecondary
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(state.items, key = { it.id }) { item ->
                                    ItemWireRow(
                                        item = item,
                                        caseStyle = caseStyle,
                                        onClick = { onItemClick(item.id) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemWireRow(
    item: Item,
    caseStyle: TextCaseStyle,
    onClick: () -> Unit
) {
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
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = LineColor,
                        style = Stroke(width = Wire)
                    )
                }

                when {
                    !item.symbol.isNullOrBlank() -> {
                        Text(
                            text = item.symbol,
                            color = TerminalGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    !item.iconKey.isNullOrBlank() -> {
                        val iconVector = when (item.iconKey) {
                            "code" -> Icons.Outlined.Code
                            "terminal" -> Icons.Outlined.Terminal
                            "bug" -> Icons.Outlined.BugReport
                            "cloud" -> Icons.Outlined.Cloud
                            "storage" -> Icons.Outlined.Storage
                            "palette" -> Icons.Outlined.Palette
                            "phone" -> Icons.Outlined.PhoneAndroid
                            "security" -> Icons.Outlined.Security
                            "chart" -> Icons.Outlined.BarChart
                            "robot" -> Icons.Outlined.SmartToy
                            "game" -> Icons.Outlined.VideogameAsset
                            "build" -> Icons.Outlined.Build
                            "web" -> Icons.Outlined.Web
                            "dataset" -> Icons.Outlined.Dataset
                            "settings" -> Icons.Outlined.Settings
                            else -> Icons.Outlined.Code
                        }
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = TerminalGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = item.name.take(2).uppercase(),
                            color = TerminalGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (item.averageRating > 0) {
                        AvgRatingChip(rating = item.averageRating)
                    }
                }

                if (item.type.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.type.toDisplayCase(caseStyle),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
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
            caseStyle = TextCaseStyle.NORMAL,
            uiState = FeedUiState.Success(
                listOf(
                    Item(name = "kotlin", description = "jvm language"),
                    Item(name = "rust", description = "systems language"),
                    Item(name = "go", description = "")
                )
            ),
            onItemClick = {},
        )
    }
}