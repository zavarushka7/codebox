package com.example.codebox.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.codebox.presentation.theme.PureBlack
import com.example.codebox.presentation.theme.TerminalGreen
import com.example.codebox.presentation.theme.TextPrimary
import com.example.codebox.presentation.theme.TextSecondary

private val Hairline = 2.5f
private val Wire = 1.5f
private val Accent = 2f
private val LineColor = Color(0xFF777777)
private val LineMuted = Color(0xFF555555)
private val StarEmpty = Color(0xFF333333)

@Composable
fun BlinkingCursor() {
    Text(
        text = "█",
        color = TerminalGreen,
        style = MaterialTheme.typography.displayLarge
    )
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
private fun BottomNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) TerminalGreen else TextSecondary,
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun WireBottomBar(
    currentTab: String,
    onFeed: () -> Unit,
    onSearch: () -> Unit,
    onProfile: () -> Unit
) {
    Column {
        HorizontalLine()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(PureBlack),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavIcon(icon = Icons.Outlined.Home,
                label = "feed",

                selected = currentTab == "feed",
                onClick = onFeed)
            BottomNavIcon(icon = Icons.Outlined.Search,
                label = "search",
                selected = currentTab == "search",
                onClick = onSearch)
            BottomNavIcon(icon = Icons.Outlined.Person,
                label = "profile",
                selected = currentTab == "profile",
                onClick = onProfile)

        }
    }
}


@Composable
fun WireAvatar(initial: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val c = 14f
            drawRect(LineColor, topLeft = Offset.Zero, size = size, style = Stroke(width = Wire))
            drawLine(LineColor, Offset(0f, 0f), Offset(c, 0f), Accent)
            drawLine(LineColor, Offset(0f, 0f), Offset(0f, c), Accent)
            drawLine(LineColor, Offset(w - c, 0f), Offset(w, 0f), Accent)
            drawLine(LineColor, Offset(w, 0f), Offset(w, c), Accent)
            drawLine(LineColor, Offset(0f, h - c), Offset(0f, h), Accent)
            drawLine(LineColor, Offset(0f, h), Offset(c, h), Accent)
            drawLine(LineColor, Offset(w - c, h), Offset(w, h), Accent)
            drawLine(LineColor, Offset(w, h - c), Offset(w, h), Accent)
        }
        Text(
            text = initial,
            color = TerminalGreen,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
fun DottedDivider(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(2.dp)) {
        drawLine(
            color = LineMuted,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = Hairline,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        )
    }
}