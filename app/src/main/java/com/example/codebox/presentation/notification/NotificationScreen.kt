package com.example.codebox.presentation.notification

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.notification.NotificationDisplay
import com.example.codebox.domain.notification.NotificationType
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.presentation.common.BlinkingCursor
import com.example.codebox.presentation.common.HorizontalLine
import com.example.codebox.presentation.common.toDisplayCase
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.theme.PureBlack
import com.example.codebox.presentation.theme.TextMuted
import com.example.codebox.presentation.theme.TextPrimary
import com.example.codebox.presentation.theme.TextSecondary


private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)
private val StarEmpty = Color(0xFF333333)

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onReviewClick: (String) -> Unit,
    onItemClick: (String) -> Unit

) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    NotificationScreenContent(
        caseStyle = caseStyle,
        uiState = uiState,
        onReviewClick = onReviewClick,
        onItemClick = onItemClick
    )
}

@Composable
fun NotificationScreenContent(
    caseStyle: TextCaseStyle,
    uiState: NotificationUiState,
    onReviewClick: (String) -> Unit,
    onItemClick: (String) -> Unit

){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// уведомления".toDisplayCase(caseStyle),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        BlinkingCursor()
                    }
                }
                uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "уведомлений пока нет".toDisplayCase(caseStyle),
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.notifications,
                        key = { it.notification.id }
                    ) {
                            display ->
                        NotificationItem(
                            display = display,
                            caseStyle = caseStyle,
                            onReviewClick = { onReviewClick(display.notification.reviewId)},
                            onItemClick = {onItemClick(display.notification.itemId)}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            }
        }
    }
}

@Composable
fun NotificationItem(
    display: NotificationDisplay,
    caseStyle: TextCaseStyle,
    onReviewClick: (String) -> Unit,
    onItemClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable{
                when (display.notification.type) {
                    NotificationType.CREATE_NEW_REVIEW,
                    NotificationType.SOMEONE_LIKED -> onReviewClick(display.reviewId)
                    else -> onItemClick()
                }
            }
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(
                avatarData = display.avatarUrl ,
                nickname = display.fromUserName,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = display.message.toDisplayCase(caseStyle),
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = display.timeAgo,
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}