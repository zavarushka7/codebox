package com.example.codebox.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.codebox.domain.ReviewWithItem
import com.example.codebox.domain.UserReview
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.feed.HorizontalLine

import com.example.codebox.presentation.feed.WireBottomBar
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire     = 1.5f
private val Accent   = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)
private val StarEmpty = Color(0xFF333333)

@Composable
fun ProfileScreen(
    onReviewClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    onFeedClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf<ReviewWithItem?>(null)}

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    ProfileScreenContent(
        uiState = uiState,

        onReviewClick = onReviewClick,
        onShowDeleteConfirm = { showDeleteConfirm = it},
        onSettingsClick = onSettingsClick,
        onFeedClick = onFeedClick,
    onSearchClick = onSearchClick,
    )
    if (showDeleteConfirm != null){
        DeleteConfirmDialog(
            review = showDeleteConfirm!!,
            onConfirm = {
                viewModel.deleteReview(showDeleteConfirm!!.review.itemId)
                showDeleteConfirm = null
            },
            onDismiss = { showDeleteConfirm = null }
        )
    }
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,

    onReviewClick: (String) -> Unit,
    onShowDeleteConfirm: (ReviewWithItem) -> Unit,
    onSettingsClick: () -> Unit,
    onFeedClick: () -> Unit,
    onSearchClick: () -> Unit,
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
                // Верхний ряд
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "// профиль",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "настройки",
                        tint = TextSecondary,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                            .clickable { onSettingsClick() }
                    )
                }

                // ← отступ между текстом и линией
                Spacer(modifier = Modifier.height(8.dp))

                HorizontalLine(strokeWidth = Hairline)
            }
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is ProfileUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            BlinkingCursor()
                        }
                    }

                    is ProfileUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "// error",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    state.message,
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    is ProfileUiState.Success -> {
                        ProfileBody(
                            state = state,
                            onReviewClick = onReviewClick,
                            onShowDeleteConfirm = onShowDeleteConfirm
                        )
                    }
                }

            }
            WireBottomBar(
                    currentTab = "profile",
            onFeed = onFeedClick,
            onSearch = onSearchClick,
            onProfile = {}
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
    icon: ImageVector,
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
fun DeleteConfirmDialog(
    review: ReviewWithItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack)   // ← сплошной чёрный, не прозрачный
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
                    .clickable(enabled = false) { }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRect(
                        color = LineColor,
                        topLeft = Offset.Zero,
                        size = size,
                        style = Stroke(width = Wire)
                    )
                    val c = 14f
                    val w = size.width
                    val h = size.height
                    drawLine(LineColor, Offset(0f, 0f), Offset(c, 0f), Accent)
                    drawLine(LineColor, Offset(0f, 0f), Offset(0f, c), Accent)
                    drawLine(LineColor, Offset(w - c, 0f), Offset(w, 0f), Accent)
                    drawLine(LineColor, Offset(w, 0f), Offset(w, c), Accent)
                    drawLine(LineColor, Offset(0f, h - c), Offset(0f, h), Accent)
                    drawLine(LineColor, Offset(0f, h), Offset(c, h), Accent)
                    drawLine(LineColor, Offset(w - c, h), Offset(w, h), Accent)
                    drawLine(LineColor, Offset(w, h - c), Offset(w, h), Accent)
                }

                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "// удалить отзыв?",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = review.itemName,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    WireButton(
                        text = "[ удалить ]",
                        onClick = onConfirm,
                        isAccent = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    WireButton(
                        text = "[ отмена ]",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
@Composable
private fun ProfileBody(
    state: ProfileUiState.Success,

    onReviewClick: (String) -> Unit,
    onShowDeleteConfirm: (ReviewWithItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            Column(){
                // В ProfileBody, вместо if/else с AsyncImage:
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = state.description,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(){
                val countReview = state.reviews.count().toString()
                Text(
                    text = "Количество ревью: $countReview",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TerminalGreen
                )
            }

        }

        DottedDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "// мои ревью",
            style = MaterialTheme.typography.titleMedium,
            color = TextMuted,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.reviews.isEmpty()) {
            Text(
                text = "// пока нет оценок",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp)
            )
        } else {
            state.reviews.forEach { rw ->
                ReviewWireRow(
                    rw = rw,
                    onEditClick = { onReviewClick(rw.review.itemId) },
                    onDeleteClick = { onShowDeleteConfirm(rw) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }


    }
}

// ─── Карточка отзыва ─────────────────────────────────────
@Composable
fun ReviewWireRow(
    rw: ReviewWithItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 12.dp)
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
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (expanded) "[-]" else "[...]", // ← стрелка изменена
                    color = TerminalGreen,
                    fontSize = 22.sp,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "редактировать",
                        color = TerminalGreen,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clickable {
                                expanded = false
                                onEditClick()
                            }
                            .padding(vertical = 2.dp)
                    )
                    Text(
                        text = "удалить",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clickable {
                                expanded = false
                                onDeleteClick()
                            }
                            .padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WireAvatar(initial: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val c = 14f
            drawRect(LineColor, topLeft = Offset.Zero, size = size, style = Stroke(width = Wire))
            drawLine(LineColor, Offset(0f, 0f), Offset(c, 0f), Accent)
            drawLine(LineColor, Offset(0f, 0f), Offset(0f, c), Accent)
            drawLine(LineColor, Offset(w - c, 0f), Offset(w, 0f), Accent)
            drawLine(LineColor, Offset(w, 0f), Offset(w, c), Accent)
            drawLine(LineColor, Offset(0f, h - c), Offset(0f, h), Accent)
            drawLine(LineColor, Offset(0f, h), Offset(c, h), Accent)
            drawLine(LineColor, Offset(w - c, h), Offset(w, h), Accent)
            drawLine(LineColor, Offset(w, h - c), Offset(w, h), Accent)
        }
        Text(
            text = initial,
            color = TerminalGreen,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
fun DottedDivider(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(2.dp)) {
        drawLine(
            color = LineMuted,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = Hairline,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        )
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
            .height(56.dp)
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
            style = MaterialTheme.typography.titleMedium
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
fun ProfileScreenPreview() {
    CodeboxTheme {
        ProfileScreenContent(
            uiState = ProfileUiState.Success(
                email = "dev@codebox.app",
                nickname = "codeNinja",
                reviews = listOf(
                    ReviewWithItem(
                        review = UserReview(
                            userId = "u1", itemId = "i1",
                            comment = "отличный язык, очень нравится работать с корутинами",
                            rating = 5
                        ),
                        itemName = "kotlin"
                    ),
                    ReviewWithItem(
                        review = UserReview(
                            userId = "u1", itemId = "i2",
                            comment = "сложно с памятью, но мощно",
                            rating = 3
                        ),
                        itemName = "rust"
                    ),
                    ReviewWithItem(
                        review = UserReview(
                            userId = "u1", itemId = "i3",
                            comment = "",
                            rating = 4
                        ),
                        itemName = "go"
                    )
                ),
                avatarUrl = "",
                description = "описание"
            ),


            onReviewClick = {},
            onShowDeleteConfirm = {},
            onSettingsClick = {},
            onSearchClick = {},
            onFeedClick = {}
        )
    }
}