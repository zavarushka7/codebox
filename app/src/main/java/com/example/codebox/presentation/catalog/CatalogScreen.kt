package com.example.codebox.presentation.catalog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val LineColor = Color(0xFF777777)

@Composable
fun CatalogScreen(
    onBack: () -> Unit,
    onItemClick: (String) -> Unit,
    onSearchByTypeClick: () -> Unit,
    onTypeClick: (String) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mode = viewModel.mode
    val typeFilter = viewModel.typeFilter

    CatalogScreenContent(
        mode = mode,
        query = query,
        typeFilter = typeFilter,
        onQueryChange = viewModel::onQueryChange,
        uiState = uiState,
        onBack = onBack,
        onItemClick = onItemClick,
        onSearchByTypeClick = onSearchByTypeClick,
        onTypeClick = onTypeClick
    )
}

@Composable
fun CatalogScreenContent(
    mode: CatalogMode,
    query: String,
    typeFilter: String,
    onQueryChange: (String) -> Unit,
    uiState: CatalogUiState,
    onBack: () -> Unit,
    onItemClick: (String) -> Unit,
    onSearchByTypeClick: () -> Unit,
    onTypeClick: (String) -> Unit
) {
    val title = when (mode) {
        CatalogMode.SEARCH -> "// поиск"
        CatalogMode.TYPES_LIST -> "// типы данных"
        CatalogMode.BY_TYPE -> "// тип: ${typeFilter.ifBlank { "все" }}"
        CatalogMode.TOP_RATED -> "// топ рейтинг"
        CatalogMode.LOWEST_RATED -> "// низкий рейтинг"
    }

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
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "< назад",
                        color = TerminalGreen,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable { onBack() }
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalLine(strokeWidth = Hairline)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Поле поиска (только для SEARCH) ──
            if (mode == CatalogMode.SEARCH) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("// введите запрос", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = TerminalGreen
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(0.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalGreen,
                        unfocusedBorderColor = LineColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = TerminalGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(26.dp))
            }

            // ── Контент ──
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is CatalogUiState.Idle -> {
                        if (mode == CatalogMode.SEARCH) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSearchByTypeClick() }
                            ) {
                                Text(
                                    text = "тип данных",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                    is CatalogUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            BlinkingCursor()
                        }
                    }
                    is CatalogUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "// error",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = state.message,
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    is CatalogUiState.TypesSuccess -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.types, key = { it }) { type ->
                                TypeRow(
                                    type = type,
                                    onClick = { onTypeClick(type) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    is CatalogUiState.Success -> {
                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "// ничего не найдено",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(state.items, key = { it.id }) { item ->
                                    CatalogItemRow(
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
        }
    }
}

@Composable
private fun TypeRow(
    type: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        // Рамка
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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = type.lowercase(),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CatalogItemRow(
    item: Item,
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
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Квадрат с символом/иконкой ──
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

            // ── Название + тип ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.lowercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                if (item.type.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // ── Рейтинг (для топ/низ) ──
            if (item.averageRating > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★",
                        color = TerminalGreen,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "%.1f".format(item.averageRating),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 2.dp)
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
fun CatalogScreenPreview() {
    CodeboxTheme {
        CatalogScreenContent(
            mode = CatalogMode.TYPES_LIST,
            query = "",
            typeFilter = "",
            onQueryChange = {},
            uiState = CatalogUiState.TypesSuccess(
                listOf(
                    "язык программирования",
                    "профессия",
                    "синтаксическая конструкция",
                    "инструмент"
                )
            ),
            onBack = {},
            onSearchByTypeClick = {},
            onItemClick = {},
            onTypeClick = {}
        )
    }
}