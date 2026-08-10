package com.example.codebox.presentation.create_item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun CreateItemScreen(
    onSaveItem: (Item) -> Unit,
    viewModel: CreateItemViewModel = hiltViewModel()
) {
    CreateItemScreenContent(
        name = viewModel.name.value,
        description = viewModel.description.value,
        nameError = viewModel.nameError.value,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSave = {
            val item = viewModel.validateAndCreate()
            if (item != null) onSaveItem(item)
        }
    )
}

@Composable
fun CreateItemScreenContent(
    name: String,
    description: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "// name: String",
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
            text = "// description: String?",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            minLines = 2,
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
            Text("save(item)")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun CreateItemScreenPreview() {
    CodeboxTheme {
        CreateItemScreenContent(
            name = "rust",
            description = "systems programming language",
            nameError = null,
            onNameChange = {},
            onDescriptionChange = {},
            onSave = {}
        )
    }
}