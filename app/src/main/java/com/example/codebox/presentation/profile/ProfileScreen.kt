package com.example.codebox.presentation.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.domain.award.AwardDisplay
import com.example.codebox.domain.review.ReviewWithItem
import com.example.codebox.domain.review.UserReview
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.theme.*
import kotlinx.coroutines.launch

private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)
private val StarEmpty = Color(0xFF333333)

@Composable
fun ProfileScreen(
    userId: String? = null,
    onReviewClick: (String) -> Unit,
    onEditReviewClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavouriteClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf<ReviewWithItem?>(null) }
    val isReadOnly = viewModel.isReadOnly

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    ProfileScreenContent(
        caseStyle = caseStyle,
        uiState = uiState,
        onReviewClick = onReviewClick,
        onEditReviewClick = onEditReviewClick,
        onShowDeleteConfirm = { showDeleteConfirm = it },
        onSettingsClick = onSettingsClick,
        isReadOnly = isReadOnly,
        onBack = onBack,
        onAddFavouriteClick = onAddFavouriteClick
    )

    if (showDeleteConfirm != null) {
        DeleteConfirmDialog(
            review = showDeleteConfirm!!,
            onConfirm = {
                viewModel.deleteReview(showDeleteConfirm!!.review.itemId)
                showDeleteConfirm = null
            },
            onDismiss = { showDeleteConfirm = null },
            caseStyle = caseStyle
        )
    }
}

@Composable
fun ProfileScreenContent(
    caseStyle: TextCaseStyle,
    uiState: ProfileUiState,
    onReviewClick: (String) -> Unit,
    onEditReviewClick: (String) -> Unit,
    onShowDeleteConfirm: (ReviewWithItem) -> Unit,
    onSettingsClick: () -> Unit,
    isReadOnly: Boolean,
    onBack: () -> Unit,
    onAddFavouriteClick: () -> Unit
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
            // ─── Шапка ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 12.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (isReadOnly) {
                        Text(
                            text = "< назад",
                            color = TerminalGreen,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable { onBack() }
                        )
                    }
                    Text(
                        text = "// профиль ${(uiState as? ProfileUiState.Success)?.nickname.orEmpty()}".toDisplayCase(caseStyle),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (!isReadOnly) {
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
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalLine(strokeWidth = Hairline)
            }

            // ─── Контент ───
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        BlinkingCursor()
                    }
                }
                is ProfileUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "// error".toDisplayCase(caseStyle),
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
                        onShowDeleteConfirm = onShowDeleteConfirm,
                        onEditReviewClick = onEditReviewClick,
                        caseStyle = caseStyle,
                        isReadOnly = isReadOnly,
                        onAddFavouriteClick = onAddFavouriteClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    review: ReviewWithItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    caseStyle: TextCaseStyle,
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
                .background(PureBlack)
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
                        text = "// удалить отзыв?".toDisplayCase(caseStyle),
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
                        text = "[ удалить ]".toDisplayCase(caseStyle),
                        onClick = onConfirm,
                        isAccent = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    WireButton(
                        text = "[ отмена ]".toDisplayCase(caseStyle),
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
    onEditReviewClick: (String) -> Unit,
    onShowDeleteConfirm: (ReviewWithItem) -> Unit,
    caseStyle: TextCaseStyle,
    isReadOnly: Boolean,
    onAddFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val tabs = listOf(
        "ревью".toDisplayCase(caseStyle),
        "награды".toDisplayCase(caseStyle)
    )

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = TerminalGreen,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = TerminalGreen,
                    height = 2.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index) TerminalGreen else TextMuted,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> ProfileReviewsPage(
                    state = state,
                    caseStyle = caseStyle,
                    isReadOnly = isReadOnly,
                    onReviewClick = onReviewClick,
                    onEditReviewClick = onEditReviewClick,
                    onShowDeleteConfirm = onShowDeleteConfirm,
                    onAddFavouriteClick = onAddFavouriteClick
                )
                1 -> AwardsPage(
                    awards = state.awards,
                    caseStyle = caseStyle
                )
            }
        }
    }
}

@Composable
private fun ProfileReviewsPage(
    state: ProfileUiState.Success,
    caseStyle: TextCaseStyle,
    isReadOnly: Boolean,
    onReviewClick: (String) -> Unit,
    onEditReviewClick: (String) -> Unit,
    onShowDeleteConfirm: (ReviewWithItem) -> Unit,
    onAddFavouriteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
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
                    state.email?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(18.dp))
                Column {
                    val countReview = state.reviews.count().toString()
                    Text(
                        text = "количество ревью:".toDisplayCase(caseStyle) + countReview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TerminalGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.description,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "...",
                    color = TerminalGreen,
                    fontSize = 22.sp,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Любимая четверка ───
            if (!isReadOnly) {
                FavouriteFourSection(
                    favouriteFour = state.favouriteFour,
                    onAddClick = onAddFavouriteClick,
                    caseStyle = caseStyle
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            DottedDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (!isReadOnly) "// мои ревью".toDisplayCase(caseStyle) else "// ревью".toDisplayCase(caseStyle),
                style = MaterialTheme.typography.titleMedium,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.reviews.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "// пока нет оценок".toDisplayCase(caseStyle),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            items(state.reviews, key = { it.review.itemId }) { rw ->
                ReviewWireRow(
                    rw = rw,
                    onCardClick = { onReviewClick(rw.review.itemId) },
                    onEditClick = { onEditReviewClick(rw.review.itemId) },
                    onDeleteClick = { onShowDeleteConfirm(rw) },
                    isReadOnly = isReadOnly
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AwardsPage(
    awards: List<AwardDisplay>,
    caseStyle: TextCaseStyle
) {
    if (awards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "// пока нет наград".toDisplayCase(caseStyle),
                color = TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
        ) {
            items(awards, key = { it.award.key }) { display ->
                AwardItem(
                    display = display,
                    caseStyle = caseStyle,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

// ============================== ИСПРАВЛЕННЫЙ БЛОК ==============================

@Composable
fun FavouriteFourSection(
    favouriteFour: List<Item>,
    onAddClick: () -> Unit,
    caseStyle: TextCaseStyle
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "// любимая четверка".toDisplayCase(caseStyle),
            style = MaterialTheme.typography.titleMedium,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (index in 0 until 4) {
                val item = favouriteFour.getOrNull(index)
                FavouriteSlot(
                    item = item,
                    onClick = { if (item == null) onAddClick() },
                    modifier = Modifier.weight(1f)   // 👈 равномерное распределение
                )
            }
        }
    }
}

@Composable
fun FavouriteSlot(
    item: Item?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)          // фиксированная высота
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = LineColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = Wire)
            )
        }

        if (item == null) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Добавить",
                tint = TextMuted,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name,
                    color = TerminalGreen,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Text(
                    text = item.type,
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// ============================== КОНЕЦ ИСПРАВЛЕННОГО БЛОКА ==============================

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AwardItem(
    display: AwardDisplay,
    caseStyle: TextCaseStyle,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "flip_animation"
    )

    val isUnlocked = display.isUnlocked

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .then(
                    if (isUnlocked) {
                        Modifier
                    } else {
                        Modifier
                            .blur(12.dp)
                            .alpha(0.4f)
                    }
                )
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8f * size.height
                }
                .clickable(enabled = isUnlocked) {
                    if (isUnlocked) isFlipped = !isFlipped
                }
        ) {
            WireframeBox(
                isAccent = isUnlocked,
                isPulsing = isUnlocked,
                pulse = pulse
            )
            AnimatedContent(
                targetState = isFlipped,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { flipped ->
                if (flipped && isUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = -1f
                            }
                    ) {
                        AwardBackSide(
                            display = display,
                            caseStyle = caseStyle
                        )
                    }
                } else {
                    AwardFrontSide(
                        display = display,
                        caseStyle = caseStyle,
                        isUnlocked = isUnlocked
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = display.award.name.toDisplayCase(caseStyle),
            color = if (isUnlocked) TerminalGreen else TextMuted,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            fontSize = 14.sp
        )
    }
}

@Composable
fun WireframeBox(
    isAccent: Boolean = false,
    isPulsing: Boolean = false,
    pulse: Float = 1f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val c = 14f
            val lineColor = if (isAccent) TerminalGreen else LineColor
            val strokeWidth = if (isAccent) Accent else Wire

            drawRect(
                color = lineColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = strokeWidth)
            )

            drawLine(lineColor, Offset(0f, 0f), Offset(c, 0f), Accent)
            drawLine(lineColor, Offset(0f, 0f), Offset(0f, c), Accent)
            drawLine(lineColor, Offset(w - c, 0f), Offset(w, 0f), Accent)
            drawLine(lineColor, Offset(w, 0f), Offset(w, c), Accent)
            drawLine(lineColor, Offset(0f, h - c), Offset(0f, h), Accent)
            drawLine(lineColor, Offset(0f, h), Offset(c, h), Accent)
            drawLine(lineColor, Offset(w - c, h), Offset(w, h), Accent)
            drawLine(lineColor, Offset(w, h - c), Offset(w, h), Accent)

            if (isPulsing) {
                drawRect(
                    color = TerminalGreen.copy(alpha = 0.1f * pulse),
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = strokeWidth * 2)
                )
            }
        }
    }
}

@Composable
fun AwardFrontSide(
    display: AwardDisplay,
    caseStyle: TextCaseStyle,
    isUnlocked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        AwardIconManager.getIconRes(display.award.iconKey)?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = display.award.name,
                modifier = Modifier.size(56.dp),
                tint = if (isUnlocked) Color.Unspecified else Color.Gray
            )
        }

        if (!isUnlocked) {
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "locked",
                modifier = Modifier.size(16.dp),
                tint = TextMuted.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun AwardBackSide(
    display: AwardDisplay,
    caseStyle: TextCaseStyle
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack.copy(alpha = 0.95f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = display.currentDescription.toDisplayCase(caseStyle),
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                maxLines = 3
            )

            display.unlockedAt?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it.toDate().toString().substring(0, 10),
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ReviewWireRow(
    rw: ReviewWithItem,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isReadOnly: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onCardClick)
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

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "...",
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
                    if (!isReadOnly) {
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
                description = "описание",
                awards = emptyList(),
                favouriteFour = emptyList()
            ),
            onReviewClick = {},
            onShowDeleteConfirm = {},
            onSettingsClick = {},
            onEditReviewClick = {},
            caseStyle = TextCaseStyle.NORMAL,
            isReadOnly = false,
            onBack = {},
            onAddFavouriteClick = {}
        )
    }
}