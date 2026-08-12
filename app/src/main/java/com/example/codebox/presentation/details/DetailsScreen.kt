package com.example.codebox.presentation.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.codebox.domain.Item
import com.example.codebox.domain.ReviewWithAuthor
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)
private val StarEmpty = Color(0xFF333333)

@Composable
fun DetailScreen(
    itemId: String,
    onBack: () -> Unit,
    onRateClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.load(itemId)
    }

    when (val state = uiState) {
        is DetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BlinkingCursor()
            }
        }
        is DetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = TextPrimary)
            }
        }
        is DetailUiState.Success -> {
            DetailScreenContent(
                item = state.item,
                reviews = state.reviews,
                onRateClick = onRateClick,
                onBack = onBack
            )
        }
    }
}

@Composable
fun DetailScreenContent(
    item: Item,
    reviews: List<ReviewWithAuthor>,
    onRateClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
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
                val type = item.type
                Text(
                    text = "// переменная: $type",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Картинка + название + описание ──
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = LineColor,
                        topLeft = Offset.Zero,
                        size = size,
                        style = Stroke(width = Wire)
                    )
                }
                AsyncImage(
                    model = item.imageUrl ?: "https://via.placeholder.com/120",
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.lowercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalLine(strokeWidth = Hairline)
        Spacer(modifier = Modifier.height(16.dp))

        // ── Кнопка оценить ──
        WireButton(
            text = "[ оценить ]",
            onClick = onRateClick,
            isAccent = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalLine(strokeWidth = Hairline)
        Spacer(modifier = Modifier.height(16.dp))

        // ── Заголовок ревью ──
        Text(
            text = "// ревью",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (reviews.isEmpty()) {
            Text(
                text = "// пока нет оценок",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            reviews.forEach { review ->
                PublicReviewRow(review = review)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Карточка ревью с прямоугольной обводкой ──
@Composable
fun PublicReviewRow(review: ReviewWithAuthor) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = LineColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = Wire)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.authorName.lowercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TerminalGreen
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★".repeat(review.rating),
                        color = TerminalGreen,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "☆".repeat(5 - review.rating),
                        color = StarEmpty,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (review.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
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
    Canvas(modifier = modifier.fillMaxWidth().height(2.dp)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun WireButton(
    text: String,
    onClick: () -> Unit,
    isAccent: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = if (isAccent) TerminalGreen else LineColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = if (isAccent) Accent else Wire)
            )
        }
        Text(
            text = text,
            color = if (isAccent) TerminalGreen else TextPrimary,
            style = MaterialTheme.typography.labelLarge
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
fun DetailScreenPreview() {
    CodeboxTheme {
        DetailScreenContent(
            item = Item(
                id = "1",
                name = "kotlin",
                type = "язык",
                description = "statically typed programming language",
                imageUrl = null
            ),
            reviews = listOf(
                ReviewWithAuthor(
                    userId = "u2",
                    itemId = "1",
                    comment = "корутины — топ, использую каждый день",
                    rating = 5,
                    authorName = "codeNinja"
                ),
                ReviewWithAuthor(
                    userId = "u3",
                    itemId = "1",
                    comment = "",
                    rating = 3,
                    authorName = "javaVeteran"
                )
            ),
            onRateClick = {},
            onBack = {}
        )
    }
}