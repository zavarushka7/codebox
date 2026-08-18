package com.example.codebox.presentation.likes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codebox.domain.like.LikeUser
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.presentation.common.*
import com.example.codebox.presentation.components.AvatarImage
import com.example.codebox.presentation.theme.*

@Composable
fun LikesListScreen(
    onBack: () -> Unit,
    onUserClick: (String) -> Unit,
    viewModel: LikesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val caseStyle by viewModel.textCaseStyle.collectAsStateWithLifecycle()

    LikesListContent(
        uiState = uiState,
        caseStyle = caseStyle,
        onBack = onBack,
        onUserClick = onUserClick
    )
}

@Composable
fun LikesListContent(
    uiState: LikesUiState,
    caseStyle: TextCaseStyle,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit
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
                    text = "// лайки".toDisplayCase(caseStyle),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalLine(strokeWidth = 2.5f)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is LikesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BlinkingCursor()
                }
            }
            is LikesUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is LikesUiState.Success -> {
                if (uiState.users.isEmpty()) {
                    Text(
                        text = "// пока нет лайков".toDisplayCase(caseStyle),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                } else {
                    uiState.users.forEach { user ->
                        LikeUserRow(
                            user = user,
                            onClick = { onUserClick(user.userId) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LikeUserRow(
    user: LikeUser,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarImage(
            avatarData = user.avatarUrl,
            nickname = user.nickname,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = user.nickname,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}