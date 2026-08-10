package com.example.codebox.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.codebox.presentation.details.DetailViewModel
import com.example.codebox.presentation.theme.GoldStar
import com.example.codebox.presentation.theme.TerminalGreen
import androidx.compose.ui.tooling.preview.Preview
import com.example.codebox.presentation.theme.CodeboxTheme
import com.example.codebox.presentation.theme.TerminalGreenBorder
import com.example.codebox.presentation.theme.TerminalGreenDark
import com.example.codebox.presentation.theme.TextMuted
import com.example.codebox.presentation.theme.TextSecondary

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsStateWithLifecycle()
    val review by viewModel.review.collectAsStateWithLifecycle()

    if (item == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TerminalGreen)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Общие данные (только для чтения) ──
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = item!!.imageUrl ?: "https://via.placeholder.com/120",
                contentDescription = item!!.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item!!.name.lowercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (item!!.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item!!.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Divider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // ── Личный комментарий (редактируемый) ──
        Text(
            text = "// my_comment: String?",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = review.comment,
            onValueChange = viewModel::onCommentChange,
            minLines = 3,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Личная оценка (кликабельные звёзды) ──
        Text(
            text = "// my_rating: Int (1..5)",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            (1..5).forEach { star ->
                Text(
                    text = if (star <= review.rating) "★" else "☆",
                    fontSize = 32.sp,
                    color = if (star <= review.rating) GoldStar else TextMuted,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { viewModel.onRatingChange(star) }
                )
            }
            Text(
                text = "  ${review.rating}/5",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Сохранить личный отзыв ──
        Button(
            onClick = {
                viewModel.saveReview()
                onBack()
            },
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TerminalGreenDark,
                contentColor = TerminalGreen
            ),
            border = BorderStroke(1.dp, TerminalGreenBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("save_my_review()")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("navigateBack()")
        }
    }
}