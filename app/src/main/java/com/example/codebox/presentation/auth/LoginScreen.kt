package com.example.codebox.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.R
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.theme.*

private val Hairline = 2.5f
private val LineColor = Color(0xFF777777)

@Composable
fun LoginScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()

    LoginScreenContent(
        caseStyle = caseStyle,
        email = viewModel.email.value,
        password = viewModel.password.value,
        error = viewModel.error.value,
        isLoading = viewModel.isLoading.value,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignIn = { viewModel.signIn(onAuthSuccess) },
        onSignUp = { viewModel.signUp(onAuthSuccess) }
    )
}

@Composable
fun LoginScreenContent(
    caseStyle: TextCaseStyle,
    email: String,
    password: String,
    error: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.codebox_auth).toDisplayCase(caseStyle),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = Hairline)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "// email: String",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "// password: String",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
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
                text = error.toDisplayCase(caseStyle),
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
                BlinkingCursor()
            }
        } else {
            WireButton(
                text = "signIn()".toDisplayCase(caseStyle),
                onClick = onSignIn,
                isAccent = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            WireButton(
                text = "signUp()".toDisplayCase(caseStyle),
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5", backgroundColor = 0xFF000000)
@Composable
fun LoginScreenPreview() {
    CodeboxTheme {
        LoginScreenContent(
            caseStyle = TextCaseStyle.NORMAL,
            email = "dev@codebox.app",
            password = "password123",
            error = null,
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onSignIn = {},
            onSignUp = {}
        )
    }
}