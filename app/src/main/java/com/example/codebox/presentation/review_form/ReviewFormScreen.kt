package com.example.codebox.presentation.review_form

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.Item
import com.example.codebox.domain.TextCaseStyle
import com.example.codebox.domain.UserReview
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val LineColor = Color(0xFF777777)

@Composable
fun ReviewFormScreen(
    onBack: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onShowLikes: (String, String) -> Unit, // ← переход на экран лайков
    viewModel: ReviewFormViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsStateWithLifecycle()
    val review by viewModel.review.collectAsStateWithLifecycle()
    val authorName by viewModel.authorName.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()
    val isReadOnly = viewModel.isReadOnly
    val authorAvatarUrl by viewModel.authorAvatarUrl.collectAsStateWithLifecycle()
    val likeCount by viewModel.likeCount.collectAsStateWithLifecycle()
    val isLiked by viewModel.isLikedByMe.collectAsStateWithLifecycle()

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BlinkingCursor()
        }
        return
    }

    ReviewFormContent(
        authorAvatarUrl = authorAvatarUrl,
        caseStyle = caseStyle,
        item = item!!,
        review = review,
        authorName = authorName,
        isReadOnly = isReadOnly,
        onCommentChange = viewModel::onCommentChange,
        onRatingChange = viewModel::onRatingChange,
        likeCount = likeCount,
        isLiked = isLiked,
        onLikeClick = viewModel::toggleLike,        // ← тоггл через ViewModel
        onShowLikes = {                            // ← навигация на список
            onShowLikes(item!!.id, review.userId)
        },
        onSave = {
            viewModel.saveReview()
            onBack()
        },
        onBack = onBack,
        onAuthorClick = onAuthorClick
    )
}

@Composable
fun ReviewFormContent(
    authorAvatarUrl: String = "",
    caseStyle: TextCaseStyle,
    item: Item,
    review: UserReview,
    authorName: String,
    isReadOnly: Boolean,
    onCommentChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onAuthorClick: (String) -> Unit,
    likeCount: Int = 0,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},   // ← теперь без параметров
    onShowLikes: () -> Unit = {},   // ← новый колбэк для счётчика
) {
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
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "< назад",
                    color = TerminalGreen,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                )
                Text(
                    text = if (isReadOnly) "// ревью ${authorName}".toDisplayCase(caseStyle) else "// оценить".toDisplayCase(caseStyle),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
                if (!isReadOnly) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "сохранить",
                        tint = TerminalGreen,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                            .clickable { onSave() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isReadOnly) {
            Row(modifier = Modifier.clickable { onAuthorClick(review.userId) },
                verticalAlignment = Alignment.CenterVertically) {
                AvatarImage(
                    avatarData = authorAvatarUrl,
                    nickname = authorName,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = item.name,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isReadOnly) "// rating: Int" else "// my_rating: Int",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            (1..5).forEach { star ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Text(
                        text = if (star <= review.rating) "★" else "☆",
                        fontSize = 32.sp,
                        color = if (star <= review.rating) TerminalGreen else TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (!isReadOnly) Modifier.clickable { onRatingChange(star) } else Modifier)
                    )
                    Text(
                        text = (star - 1).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TerminalGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Text(
                text = "  ${review.rating}/5",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isReadOnly) "// comment: String?" else "// my_comment: String?",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = review.comment,
            onValueChange = onCommentChange,
            enabled = !isReadOnly,
            minLines = 3,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = LineColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledBorderColor = LineColor,
                disabledTextColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        LikeButton(
            isLiked = isLiked,
            count = likeCount,
            onToggle = onLikeClick,   // ← сердечко тогглит
            onCountClick = onShowLikes // ← счётчик ведёт на экран лайков
        )
    }
}

@Composable
fun LikeButton(
    isLiked: Boolean,
    count: Int,
    onToggle: () -> Unit,
    onCountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetScale by remember { mutableFloatStateOf(1f) }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "like_scale"
    )

    val iconColor = if (isLiked) TerminalGreen else TextSecondary
    val heartIcon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ─── Сердце — зона тапа 36×36, по центру иконка 20×20 ───
        Box(
            modifier = Modifier
                .size(36.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            targetScale = 0.75f
                            onToggle()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = heartIcon,
                contentDescription = if (isLiked) "убрать лайк" else "поставить лайк",
                tint = iconColor,
                modifier = Modifier
                    .size(20.dp)
                    .scale(scale)
            )
        }

        LaunchedEffect(scale) {
            if (scale < 1f) targetScale = 1f
        }

        Spacer(modifier = Modifier.width(2.dp))

        // ─── Текст — отдельная зона тапа ───
        var isInitial by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) { isInitial = false }

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onCountClick() })
                }
                .padding(horizontal = 4.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    if (isInitial) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else {
                        slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
                    }
                },
                label = "like_count"
            ) { targetCount ->
                Text(
                    text = pluralizeLikes(targetCount),
                    color = iconColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun pluralizeLikes(count: Int): String {
    val lastTwo = count % 100
    val lastOne = count % 10
    val suffix = when {
        lastTwo in 11..14 -> "лайков"
        lastOne == 1 -> "лайк"
        lastOne in 2..4 -> "лайка"
        else -> "лайков"
    }
    return "$count $suffix"
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5",
    backgroundColor = 0xFF000000
)
@Composable
fun ReviewFormPreview() {
    CodeboxTheme {
        ReviewFormContent(
            caseStyle = TextCaseStyle.NORMAL,
            item = Item(
                id = "1",
                name = "kotlin",
                description = "statically typed programming language",
                symbol = "KT"
            ),
            review = UserReview(
                userId = "u1",
                itemId = "1",
                comment = "отличный язык",
                rating = 4
            ),
            onCommentChange = {},
            onRatingChange = {},
            onSave = {},
            onBack = {},
            authorName = "glofko",
            isReadOnly = true,
            authorAvatarUrl = "",
            onAuthorClick = {}
        )
    }
}