package com.example.codebox.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codebox.presentation.theme.CodeboxTheme
import com.example.codebox.presentation.theme.TerminalGreen
import com.example.codebox.presentation.theme.TerminalGreenBorder
import com.example.codebox.presentation.theme.TerminalGreenDark
import com.example.codebox.presentation.theme.TextMuted

@Composable
fun LoginScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "codebox auth",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "// email: String",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = viewModel::onEmailChange,
            singleLine = true,
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "// password: String",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = viewModel::onPasswordChange,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerminalGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.error.value != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = viewModel.error.value!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isLoading.value) {
            CircularProgressIndicator(color = TerminalGreen, modifier = Modifier.padding(vertical = 8.dp))
        } else {
            Button(
                onClick = { viewModel.signIn(onAuthSuccess) },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerminalGreenDark,
                    contentColor = TerminalGreen
                ),
                border = BorderStroke(1.dp, TerminalGreenBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("signIn()")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.signUp(onAuthSuccess) },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("signUp()")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun LoginScreenPreview() {
    CodeboxTheme {
        LoginScreen(onAuthSuccess = {})
    }
}