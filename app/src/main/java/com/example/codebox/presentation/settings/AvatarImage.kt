package com.example.codebox.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.codebox.presentation.common.WireAvatar


@Composable
fun AvatarImage(
    avatarData: String,
    nickname: String,
    modifier: Modifier = Modifier.size(100.dp)
) {
    when {
        avatarData.isBlank() -> {
            WireAvatar(
                initial = nickname.firstOrNull()?.uppercase() ?: "?",
                modifier = modifier
            )
        }
        avatarData.startsWith("http") -> {
            AsyncImage(
                model = avatarData,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        else -> {
            val bytes = Base64.decode(avatarData, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = modifier
                )
            } else {
                WireAvatar(
                    initial = nickname.firstOrNull()?.uppercase() ?: "?",
                    modifier = modifier
                )
            }
        }
    }
}