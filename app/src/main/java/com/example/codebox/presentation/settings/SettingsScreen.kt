package com.example.codebox.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.profile.WireButton
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onAvatarSelected(it, context) }
    }

    SettingsScreenContent(
        nickname = viewModel.nickname.value,
        avatarUrl = viewModel.avatarUrl.value,
        error = viewModel.error.value,
        success = viewModel.success.value,
        isLoading = viewModel.isLoading.value,
        onNicknameChange = viewModel::onNicknameChange,
        onAvatarClick = { launcher.launch("image/*") },
        onSave = {
            viewModel.saveProfile()
            onBack()
        },
        onBack = onBack,
        onSignOut = {
            viewModel.signOut()
            onSignedOut()
        }
    )
}

@Composable
fun SettingsScreenContent(
    nickname: String,
    avatarUrl: String,
    error: String?,
    success: String?,
    isLoading: Boolean,
    onNicknameChange: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
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
                    text = "// настройки",
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
            text = "[ сменить аватар ]",
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

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = TerminalGreen, style = MaterialTheme.typography.labelSmall)
        }
        if (success != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = success, color = TerminalGreen, style = MaterialTheme.typography.labelSmall)
        }
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BlinkingCursor()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        WireButton(
            text = "[ выйти из аккаунта ]",
            onClick = onSignOut,
            modifier = Modifier.padding(bottom = 20.dp)
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
            nickname = "codeNinja",
            avatarUrl = "",
            error = null,
            success = null,
            isLoading = false,
            onNicknameChange = {},
            onAvatarClick = {},
            onSave = {},
            onBack = {},
            onSignOut = {}
        )
    }
}