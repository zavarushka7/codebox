package com.example.codebox.presentation.create_item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codebox.domain.Item
import com.example.codebox.presentation.theme.CodeboxTheme
import com.example.codebox.presentation.theme.TerminalGreen
import com.example.codebox.presentation.theme.TerminalGreenBorder
import com.example.codebox.presentation.theme.TerminalGreenDark
import com.example.codebox.presentation.theme.TextMuted

/* ── Stateful wrapper (в приложении) ── */
@Composable
fun CreateItemScreen(
    onSaveItem: (Item) -> Unit,
    onCancel: () -> Unit,
    viewModel: CreateItemViewModel = hiltViewModel()
) {
    CreateItemScreenContent(
        name = viewModel.name.value,
        ratingText = viewModel.ratingText.value,
        description = viewModel.description.value,
        comment = viewModel.comment.value,
        nameError = viewModel.nameError.value,
        ratingError = viewModel.ratingError.value,
        descriptionError = viewModel.descriptionError.value,
        commentError = viewModel.commentError.value,
        onNameChange = viewModel::onNameChange,
        onRatingChange = viewModel::onRatingChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCommentChange = viewModel::onCommentChange,
        onSave = {
            val item = viewModel.validateAndCreate()
            if (item != null) onSaveItem(item)
        },
        onCancel = onCancel
    )
}

/* ── Stateless content (Preview тут работает) ── */
@Composable
fun CreateItemScreenContent(
    name: String,
    ratingText: String,
    description: String,
    comment: String,
    nameError: String?,
    ratingError: String?,
    descriptionError: String?,
    commentError: String?,
    onNameChange: (String) -> Unit,
    onRatingChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Text(
            text = "айтем",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            singleLine = true,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError != null) {
            Text(
                text = nameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "описание",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            singleLine = true,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            isError = descriptionError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (descriptionError != null) {
            Text(
                text = descriptionError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "комментарий",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            minLines = 3,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            isError = commentError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (commentError != null) {
            Text(
                text = commentError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "// rating: Int (1..5)",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = ratingText,
            onValueChange = onRatingChange,
            singleLine = true,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            isError = ratingError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (ratingError != null) {
            Text(
                text = ratingError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TerminalGreenDark,
                contentColor = TerminalGreen
            ),
            border = BorderStroke(1.dp, TerminalGreenBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("сохранить")
        }

        Button(
            onClick = onCancel,
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TerminalGreenDark,
                contentColor = TerminalGreen
            ),
            border = BorderStroke(1.dp, TerminalGreenBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("отменить")
        }
    }
}

/* ── ОДНО PREVIEW на весь экран ── */
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5"
)
@Composable
fun CreateItemScreenFullPreview() {
    CodeboxTheme {
        CreateItemScreenContent(
            name = "typescript",
            ratingText = "4",
            comment = "Комментарий",
            description = "Описание",
            commentError = null,
            descriptionError = null,
            onCancel = {},
            onCommentChange = {},
            onDescriptionChange = {},
            nameError = null,
            ratingError = null,
            onNameChange = {},
            onRatingChange = {},
            onSave = {}
        )
    }
}