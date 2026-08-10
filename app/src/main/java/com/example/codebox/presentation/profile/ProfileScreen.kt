package com.example.codebox.presentation.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.UserReview
import com.example.codebox.presentation.theme.*

// ─── Толщины линий ──────────────────────────────────────
private val Hairline = 2.5f      // тонкие разделители
private val Wire     = 1.5f    // основные контуры (было 0.5f)
private val Accent   = 2f      // уголки-«крепления»

// ─── Цвет линий (был #444444, стал #777777 — видно на чёрном) ─
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreenContent(
        uiState = uiState,
        onBack = onBack,
        onSignOut = {
            viewModel.signOut()
            onSignedOut()
        }
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
    ) {
        // ── Шапка ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 12.dp)
        ) {
            Text(
                text = "< назад",
                color = TerminalGreen,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )
            Text(
                text = "// профиль",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalLine(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                strokeWidth = Hairline
            )
        }

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
                        Text("// error", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                        Text(state.message, color = TextPrimary)
                    }
                }
            }

            is ProfileUiState.Success -> {
                ProfileBody(state = state, onSignOut = onSignOut)
            }
        }
    }
}

@Composable
private fun ProfileBody(
    state: ProfileUiState.Success,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        WireAvatar(
            initial = state.nickname.firstOrNull()?.uppercase() ?: "?",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.nickname,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Text(
            text = state.email,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))
        DottedDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "// мои оценки",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.reviews.isEmpty()) {
            Text(
                text = "// пока нет оценок",
                color = TextSecondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            state.reviews.forEach { rw ->
                ReviewWireRow(rw = rw)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        WireButton(
            text = "[ выйти из аккаунта ]",
            onClick = onSignOut,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

// ─── Мигающий курсор (заглушка вместо спиннера) ────────
@Composable
fun BlinkingCursor() {
    Text(
        text = "█",
        color = TerminalGreen,
        style = MaterialTheme.typography.displayLarge
    )
}

// ─── Аватар: квадрат с уголками-«креплениями» ──────────
@Composable
fun WireAvatar(initial: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val c = 12f

            // основной контур квадрата
            drawRect(
                color = LineColor,
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = Wire)
            )

            // уголки-«крепления» — толще и длиннее
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
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

// ─── Горизонтальная линия (вынесенный компонент) ───────
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

// ─── Точечный разделитель (крупнее, виднее) ─────────────
@Composable
fun DottedDivider(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            color = LineMuted,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = Hairline,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        )
    }
}

// ─── Строка оценки: верх + низ + маркер слева ──────────
@Composable
fun ReviewWireRow(rw: ReviewWithItem) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            val w = size.width
            val h = size.height

            // верхняя и нижняя линии — толще
            drawLine(LineColor, Offset(0f, 0f), Offset(w, 0f), Wire)
            drawLine(LineColor, Offset(0f, h), Offset(w, h), Wire)

            // вертикальная палочка-маркер слева (как на клавише табуляции)
            drawLine(LineColor, Offset(10f, h / 2 - 8f), Offset(10f, h / 2 + 8f), Accent)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp)
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rw.itemName,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "★".repeat(rw.review.rating) + "☆".repeat(5 - rw.review.rating),
                color = TerminalGreen,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ─── Кнопка-рамка (без заливки, только контур) ──────────
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

// ─── Preview ─────────────────────────────────────────────
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
                            comment = "отличный язык", rating = 5
                        ),
                        itemName = "kotlin"
                    ),
                    ReviewWithItem(
                        review = UserReview(
                            userId = "u1", itemId = "i2",
                            comment = "", rating = 3
                        ),
                        itemName = "javascript"
                    ),
                    ReviewWithItem(
                        review = UserReview(
                            userId = "u1", itemId = "i3",
                            comment = "неплохо", rating = 4
                        ),
                        itemName = "go"
                    )
                )
            ),
            onBack = {},
            onSignOut = {}
        )
    }
}