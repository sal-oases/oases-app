package com.unimib.oases.ui.components.util

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActionIcon(
    onClick: () -> Unit,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    contentDescription: String? = null,
){
    IconButton(onClick = onClick, modifier = modifier.background(backgroundColor)) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }

}