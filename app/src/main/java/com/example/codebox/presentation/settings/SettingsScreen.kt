package com.example.codebox.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val LineColor = Color(0xFF777777)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onAvatarSelected(it, context) }
    }

    SettingsScreenContent(
        caseStyle = caseStyle,
        nickname = viewModel.nickname.value,
        description = viewModel.description.value,
        avatarUrl = viewModel.avatarUrl.value,
        onNicknameChange = viewModel::onNicknameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onAvatarClick = { launcher.launch("image/*") },
        onSave = {
            viewModel.saveProfile()
            onBack()
        },
        onBack = onBack,
        onSignOut = {
            viewModel.signOut()
            onSignedOut()
        },
        onCamelCaseClick = { viewModel.setTextCaseStyle(TextCaseStyle.CAMEL_CASE) },
        onSnakeCaseClick = { viewModel.setTextCaseStyle(TextCaseStyle.SNAKE_CASE) },
        onNormalClick = { viewModel.setTextCaseStyle(TextCaseStyle.NORMAL) }
    )
}

@Composable
fun SettingsScreenContent(
    caseStyle: TextCaseStyle,
    nickname: String,
    description: String,
    avatarUrl: String,
    onNicknameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onCamelCaseClick: () -> Unit,
    onSnakeCaseClick: () -> Unit,
    onNormalClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // ── Шапка ──
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
                    text = "// настройки".toDisplayCase(caseStyle),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
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
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Аватар ──
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            AvatarImage(
                avatarData = avatarUrl,
                nickname = nickname,
                modifier = Modifier
                    .size(100.dp)
                    .clickable { onAvatarClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "[ сменить аватар ]".toDisplayCase(caseStyle),
            color = TerminalGreen,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onAvatarClick() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Никнейм ──
        Text(
            text = "// nickname: String",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            singleLine = true,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = LineColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "// description: String",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            minLines = 3,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = LineColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalLine(strokeWidth = Hairline)
        Spacer(modifier = Modifier.height(16.dp))

        // ── Стиль текста ──
        Text(
            text = "// стиль текста".toDisplayCase(caseStyle),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        WireButton(
            text = "camelCase",
            onClick = onCamelCaseClick,
            isAccent = caseStyle == TextCaseStyle.CAMEL_CASE,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        WireButton(
            text = "snake_case",
            onClick = onSnakeCaseClick,
            isAccent = caseStyle == TextCaseStyle.SNAKE_CASE,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        WireButton(
            text = "обычный",
            onClick = onNormalClick,
            isAccent = caseStyle == TextCaseStyle.NORMAL,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        WireButton(
            text = "[ выйти из аккаунта ]".toDisplayCase(caseStyle),
            onClick = onSignOut,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_5",
    backgroundColor = 0xFF000000
)
@Composable
fun SettingsScreenPreview() {
    CodeboxTheme {
        SettingsScreenContent(
            caseStyle = TextCaseStyle.NORMAL,
            nickname = "codeNinja",
            avatarUrl = "",
            onNicknameChange = {},
            onAvatarClick = {},
            onSave = {},
            onBack = {},
            onSignOut = {},
            onDescriptionChange = {},
            description = "описание",
            onCamelCaseClick = {},
            onSnakeCaseClick = {},
            onNormalClick = {}
        )
    }
}