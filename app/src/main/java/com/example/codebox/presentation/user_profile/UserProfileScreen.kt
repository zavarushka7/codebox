package com.example.codebox.presentation.user_profile

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.ReviewWithItem
import com.example.codebox.domain.TextCaseStyle
import com.example.codebox.domain.UserReview
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val LineMuted = Color(0xFF555555)
private val LineColor = Color(0xFF777777)
private val StarEmpty = Color(0xFF333333)

@Composable
fun UserProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onItemClick: (String) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.load()
    }

    UserProfileScreenContent(
        caseStyle = caseStyle,
        uiState = uiState,
        onBack = onBack,
        onItemClick = onItemClick
    )
}

@Composable
fun UserProfileScreenContent(
    caseStyle: TextCaseStyle,
    uiState: UserProfileUiState,
    onBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
    ) {
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
                    text = "// пользователь".toDisplayCase(caseStyle),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Box(modifier = Modifier.weight(1f)) {
            when (val state = uiState) {
                is UserProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BlinkingCursor()
                    }
                }
                is UserProfileUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = TextPrimary)
                    }
                }
                is UserProfileUiState.Success -> {
                    UserProfileBody(
                        caseStyle = caseStyle,
                        state = state,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}

@Composable
private fun UserProfileBody(
    caseStyle: TextCaseStyle,
    state: UserProfileUiState.Success,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        AvatarImage(
            avatarData = state.avatarUrl,
            nickname = state.nickname,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = state.nickname,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )

        if (state.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        DottedDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "// ревью".toDisplayCase(caseStyle),
            style = MaterialTheme.typography.titleMedium,
            color = TextMuted,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.reviews.isEmpty()) {
            Text(
                text = "// пока нет оценок".toDisplayCase(caseStyle),
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp)
            )
        } else {
            state.reviews.forEach { rw ->
                UserReviewRow(
                    caseStyle = caseStyle,
                    rw = rw,
                    onClick = { onItemClick(rw.review.itemId) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun UserReviewRow(
    caseStyle: TextCaseStyle,
    rw: ReviewWithItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            drawLine(LineColor, Offset(0f, 0f), Offset(w, 0f), Wire)
            drawLine(LineColor, Offset(0f, h), Offset(w, h), Wire)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = rw.itemName,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "★".repeat(rw.review.rating),
                    color = TerminalGreen,
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "☆".repeat(5 - rw.review.rating),
                    color = StarEmpty,
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (rw.review.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rw.review.comment,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
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
fun UserProfileScreenPreview() {
    CodeboxTheme {
        UserProfileScreenContent(
            caseStyle = TextCaseStyle.NORMAL,
            uiState = UserProfileUiState.Success(
                nickname = "codeNinja",
                avatarUrl = "",
                description = "android dev",
                reviews = listOf(
                    ReviewWithItem(
                        review = UserReview(userId = "u1", itemId = "i1", comment = "топ", rating = 5),
                        itemName = "kotlin"
                    )
                )
            ),
            onBack = {},
            onItemClick = {}
        )
    }
}