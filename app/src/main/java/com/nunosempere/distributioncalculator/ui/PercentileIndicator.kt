package com.nunosempere.distributioncalculator.ui.percentileindicator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nunosempere.distributioncalculator.ui.theme.SurfaceVariantSelected

@Composable
fun PercentileIndicator(
    text: String,
    blue: Boolean = false,
    selected_input: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (blue) MaterialTheme.colorScheme.primaryContainer else (if (selected_input) MaterialTheme.colorScheme.surfaceVariant else SurfaceVariantSelected),
                RoundedCornerShape(4.dp)
            )
            /*.border(
                2.dp,
                Color.White,
                RoundedCornerShape(4.dp)
            )*/
            .padding(horizontal = 12.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (selected_input) FontWeight.Bold else FontWeight.Normal,
            color = if (blue) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
