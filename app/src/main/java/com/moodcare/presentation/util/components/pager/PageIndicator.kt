package com.moodcare.presentation.util.components.pager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moodcare.presentation.theme.GRAY
import com.moodcare.presentation.theme.PURPLE

@Composable
fun PageIndicator(
    totalIndicators: Int,
    selectedIndicator: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(10.dp)
            .width(60.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalIndicators) { index ->
            val isSelected = index == selectedIndicator

            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = tween(durationMillis = 500),
                label = "WidthAnimation"
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) PURPLE else GRAY,
                animationSpec = tween(durationMillis = 500),
                label = "ColorAnimation"
            )
            Spacer(
                modifier = Modifier
                    .size(width = animatedWidth, height = 8.dp)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
            if (index != totalIndicators - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PageIndicatorPreview() {
    PageIndicator(
        totalIndicators = 3,
        selectedIndicator = 1
    )

}
