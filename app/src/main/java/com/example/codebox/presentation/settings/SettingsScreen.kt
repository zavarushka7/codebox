package com.example.codebox.presentation.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)



@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreenContent(
        nickname = viewModel.nickname.value,
        error = viewModel.error.value,
        success = viewModel.success.value,
        isLoading = viewModel.isLoading.value,
        onNicknameChange = viewModel::onNicknameChange,
        onSave = viewModel::saveProfile,
        onBack = onBack
    )
}

@Composable
fun SettingsScreenContent(
    nickname: String,
    error: String?,
    success: String?,
    isLoading: Boolean,
    onNicknameChange: (String) -> Unit,
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
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Spacer(modifier = Modifier.height(24.dp))

    
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
            Text(
                text = error,
                color = TerminalGreen,
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (success != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = success,
                color = TerminalGreen,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "█",
                    color = TerminalGreen,
                    style = MaterialTheme.typography.displayLarge
                )
            }
        } else {
            WireButton(
                text = "[ сохранить ]",
                onClick = onSave,
                isAccent = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
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
            nickname = "dev@codebox.app",
            error = null,
            success = null,
            isLoading = false,
            onNicknameChange = {},
            onSave = {},
            onBack = {}
        )
    }
}