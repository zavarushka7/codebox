package com.example.codebox.presentation.review_form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val LineColor = Color(0xFF777777)

@Composable
fun ReviewFormScreen(
    onBack: () -> Unit,
    viewModel: ReviewFormViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsStateWithLifecycle()
    val review by viewModel.review.collectAsStateWithLifecycle()
    val authorName by viewModel.authorName.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()
    val isReadOnly = viewModel.isReadOnly

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BlinkingCursor()
        }
        return
    }

    ReviewFormContent(
        caseStyle = caseStyle,
        item = item!!,
        review = review,
        authorName = authorName,
        isReadOnly = isReadOnly,
        onCommentChange = viewModel::onCommentChange,
        onRatingChange = viewModel::onRatingChange,
        onSave = {
            viewModel.saveReview()
            onBack()
        },
        onBack = onBack
    )
}

@Composable
fun ReviewFormContent(
    caseStyle: TextCaseStyle,
    item: Item,
    review: UserReview,
    authorName: String,
    isReadOnly: Boolean,
    onCommentChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
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
                    text = if (isReadOnly) "// ревью".toDisplayCase(caseStyle) else "// оценить".toDisplayCase(caseStyle),
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
            Text(
                text = "// автор: ${authorName}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = item.name,
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

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalLine(strokeWidth = Hairline)
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

        Text(
            text = if (isReadOnly) "// rating: Int (1..5)" else "// my_rating: Int (1..5)",
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


    }
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
            isReadOnly = true
        )
    }
}