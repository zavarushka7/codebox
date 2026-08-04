package com.example.codebox.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.codebox.domain.Item
import com.example.codebox.presentation.theme.CodeboxTheme
import com.example.codebox.presentation.theme.GoldStar
import com.example.codebox.presentation.theme.TerminalGreen
import com.example.codebox.presentation.theme.TerminalGreenBorder
import com.example.codebox.presentation.theme.TerminalGreenDark
import com.example.codebox.presentation.theme.TextMuted
import com.example.codebox.presentation.theme.TextSecondary

@Composable
fun DetailScreen(
    item: Item,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Верхний блок: картинка слева, текст справа ──
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = item.imageUrl ?: "https://via.placeholder.com/120",
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.lowercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (item.description?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    item.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Divider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // ── Комментарий пользователя ──
        if (item.comment?.isNotBlank() == true) {
            Text(
                text = "// user.review",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedCard(
                shape = RoundedCornerShape(0.dp),
                colors = androidx.compose.material3.CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Оценка ──
        if (item.rating > 0) {
            Text(
                text = "// user.rating",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "★".repeat(item.rating.coerceIn(0, 5)),
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldStar
                )
                Text(
                    text = "★".repeat((5 - item.rating).coerceIn(0, 5)),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextMuted
                )
                Text(
                    text = "  ${item.rating}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Кнопка назад ──
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TerminalGreenDark,
                contentColor = TerminalGreen
            ),
            border = BorderStroke(1.dp, TerminalGreenBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("navigateBack()")
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
fun DetailScreenFullPreview() {
    CodeboxTheme {
        DetailScreen(
            item = Item(
                name = "Kotlin",
                description = "Статически типизированный язык программирования для JVM, Android и браузера. Полностью совместим с Java.",
                rating = 5,
                comment = "Null safety меняет подход к разработке. Extension functions — must have. После перехода с Java назад уже не хочется.",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/7/74/Kotlin_Icon.png"
            ),
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenMinimalPreview() {
    CodeboxTheme {
        DetailScreen(
            item = Item(
                name = "Docker",
                description = "Платформа для разработки и доставки приложений в контейнерах.",
                rating = 0,
                comment = "",
                imageUrl = null
            ),
            onBack = {}
        )
    }
}