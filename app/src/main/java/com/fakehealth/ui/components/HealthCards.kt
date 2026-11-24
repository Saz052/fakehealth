package com.fakehealth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fakehealth.ui.theme.FakeHealthTypography
import com.fakehealth.R

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector,
    iconColor: Color,
    trend: String? = null,
    trendUp: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            // Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 36.sp,
                    fontWeight = FakeHealthTypography.displaySmall.fontWeight,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                if (unit.isNotBlank()) {
                    Text(
                        text = unit,
                        fontSize = 18.sp,
                        fontWeight = FakeHealthTypography.bodyLarge.fontWeight,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FakeHealthTypography.labelMedium.fontWeight,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )

            trend?.let {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (trendUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (trendUp) Color(0xFF00C853) else Color(0xFFFF3D00),
                        modifier = Modifier.size(16.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FakeHealthTypography.labelSmall.fontWeight,
                        color = if (trendUp) Color(0xFF00C853) else Color(0xFFFF3D00)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressRingCard(
    modifier: Modifier = Modifier,
    title: String,
    progress: Float, // 0..1
    value: String,
    unit: String = "",
    icon: ImageVector,
    iconColor: Color,
    ringColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Ring with value inside
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    strokeWidth = 12.dp,
                    size = 120.dp,
                    trackColor = ringColor.copy(alpha = 0.15f),
                    indicatorColor = ringColor
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = value,
                        fontSize = 32.sp,
                        fontWeight = FakeHealthTypography.displaySmall.fontWeight,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                    if (unit.isNotBlank()) {
                        Text(
                            text = unit,
                            fontSize = 14.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FakeHealthTypography.titleMedium.fontWeight,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CircularProgressIndicator(
    progress: Float,
    strokeWidth: androidx.compose.ui.unit.Dp,
    size: androidx.compose.ui.unit.Dp,
    trackColor: Color,
    indicatorColor: Color
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(size)
    ) {
        val radius = (size.value - strokeWidth.value) / 2
        val center = size.value / 2f

        // Track
        drawCircle(
            color = trackColor,
            radius = radius,
            style = androidx.compose.ui.draw.Stroke(width = strokeWidth.value, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )

        // Progress
        val sweepAngle = 360 * progress
        drawArc(
            color = indicatorColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            radius = radius,
            style = androidx.compose.ui.draw.Stroke(width = strokeWidth.value, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
    }
}

// Simple icons using Material icons
object Icons {
    object Default {
        val Steps = androidx.compose.material.icons.filled.DirectionsWalk
        val Calories = androidx.compose.material.icons.filled.LocalFireDepartment
        val Sleep = androidx.compose.material.icons.filled.Bedtime
        val HeartRate = androidx.compose.material.icons.filled.Favorite
        val TrendingUp = androidx.compose.material.icons.filled.TrendingUp
        val TrendingDown = androidx.compose.material.icons.filled.TrendingDown
    }
}