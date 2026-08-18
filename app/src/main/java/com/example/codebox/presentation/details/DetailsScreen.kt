package com.example.codebox.presentation.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.domain.review.ReviewWithAuthor
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.domain.review.UserReview
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
    onReviewAuthorClick: (String) -> Unit,
    onReviewClick: (String, String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()

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
                caseStyle = caseStyle,
                item = state.item,
                reviews = state.reviews,
                myReview = state.myReview,
                onRateClick = onRateClick,
                onBack = onBack,
                onReviewAuthorClick = onReviewAuthorClick,
                onReviewClick = onReviewClick
            )
        }
    }
}

@Composable
fun DetailScreenContent(
    caseStyle: TextCaseStyle,
    item: Item,
    reviews: List<ReviewWithAuthor>,
    myReview: UserReview?,
    onRateClick: () -> Unit,
    onBack: () -> Unit,
    onReviewAuthorClick: (String) -> Unit,
    onReviewClick: (String, String) -> Unit
) {
    val currentUserId = Firebase.auth.currentUser?.uid
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "< назад",
                        color = TerminalGreen,
                        modifier = Modifier.clickable { onBack() }
                    )
                }

                Text(
                    text = "// переменная: ${item.type.toDisplayCase(caseStyle)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Spacer(modifier = Modifier.height(16.dp))

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

                when {
                    !item.symbol.isNullOrBlank() -> {
                        Text(
                            text = item.symbol,
                            color = TerminalGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 40.sp,
                            style = MaterialTheme.typography.headlineLarge
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
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = item.name.take(2).uppercase(),
                            color = TerminalGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 40.sp,
                            style = MaterialTheme.typography.headlineLarge
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
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    if (item.averageRating > 0) {
                        AvgRatingChip(rating = item.averageRating)
                    }
                }

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

        if (myReview != null) {
            WireButton(
                text = "[ уже оценено ]".toDisplayCase(caseStyle),
                onClick = onRateClick,
                isAccent = false,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            WireButton(
                text = "[ оценить ]".toDisplayCase(caseStyle),
                onClick = onRateClick,
                isAccent = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalLine(strokeWidth = Hairline)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "// ревью".toDisplayCase(caseStyle),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (reviews.isEmpty()) {
            Text(
                text = "// пока нет оценок".toDisplayCase(caseStyle),
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            reviews.forEach { review ->
                PublicReviewRow(
                    review = review,
                    caseStyle = caseStyle,
                    isCurrentUser = review.userId == currentUserId,
                    onAuthorClick = { onReviewAuthorClick(review.userId) },
                    onClick = { onReviewClick(review.itemId, review.userId) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PublicReviewRow(
    review: ReviewWithAuthor,
    caseStyle: TextCaseStyle,
    isCurrentUser: Boolean,
    onAuthorClick: () -> Unit,
    onClick: () -> Unit
) {
    val borderColor = if (isCurrentUser) TerminalGreen else LineColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = borderColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = if (isCurrentUser) Accent else Wire)
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
            ) {           Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarImage(
                    avatarData = review.avatarUrl,
                    nickname = review.authorName,
                    modifier = Modifier.size(30.dp)
                        .clickable{ onAuthorClick() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = review.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCurrentUser) TerminalGreen else TextPrimary,
                    modifier = Modifier.clickable { onAuthorClick() }
                )
            }
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
            caseStyle = TextCaseStyle.NORMAL,
            item = Item(
                id = "1",
                name = "kotlin",
                type = "язык",
                description = "statically typed programming language",
                symbol = "KT"
            ),
            reviews = listOf(
                ReviewWithAuthor(
                    userId = "u2",
                    itemId = "1",
                    comment = "корутины — топ",
                    rating = 5,
                    authorName = "codeNinja",
                    avatarUrl = TODO(),
                    countLikes = TODO(),
                    likedBy = TODO(),
                )
            ),
            myReview = UserReview(userId = "u1", itemId = "1", comment = "класс", rating = 4),
            onRateClick = {},
            onBack = {},
            onReviewAuthorClick = {},
            onReviewClick = { _, _ -> }
        )
    }
}