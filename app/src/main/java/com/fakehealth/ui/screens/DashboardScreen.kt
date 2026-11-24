package com.fakehealth.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.fakehealth.data.DailySummary
import com.fakehealth.data.WeeklyStats
import com.fakehealth.ui.components.HealthCards.ComposableSingletons$HealthCardsKt
import com.fakehealth.ui.components.HealthCards.ProgressRingCard
import com.fakehealth.ui.components.MetricCard
import com.fakehealth.ui.components.Icons
import com.fakehealth.ui.theme.FakeHealthTheme
import com.fakehealth.ui.theme.FakeHealthTypography
import com.fakehealth.ui.MainViewModel
import com.fakehealth.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onSecretTrigger: () -> Unit
) {
    val todayRecord by viewModel.todayRecord.observeAsState(null)
    val weeklyStats by viewModel.weeklyStats.observeAsState(WeeklyStats(0, 0, 0, 0, 0f, 0f, 0))
    val last7Days by viewModel.last7Days.observeAsState(emptyList())
    val secretMode by viewModel.secretModeEnabled.observeAsState(false)
    val hasPin by viewModel.hasPin.observeAsState(false)

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var isSettingPin by remember { mutableStateOf(false) }

    val todaySummary = todayRecord?.let { com.fakehealth.data.DailySummary.fromRecord(it) }

    // Secret tap detector on the whole screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FakeHealthTheme.colorScheme.background)
            .combinedClickable(
                onClick = { /* single tap - do nothing special */ },
                onDoubleClick = { /* double tap - do nothing */ },
                onLongClick = { /* long click - do nothing */ },
                onClickLabel = null
            ) {
                // We'll use a custom approach for multi-tap
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Samsung Health",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FakeHealthTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    // Secret trigger area - tap 5x on title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        // Invisible tap detector on title area
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FakeHealthTheme.colorScheme.background,
                    titleContentColor = FakeHealthTheme.colorScheme.onSurface
                )
            )

            // Content
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main metrics row - Steps, Calories
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "걸음 수",
                            value = todaySummary?.steps.toString() ?: "0",
                            unit = "보",
                            icon = Icons.Default.Steps,
                            iconColor = Color(0xFF0066FF),
                            trend = "+12% vs 어제",
                            trendUp = true
                        ) {
                            if (secretMode) onSecretTrigger()
                        }

                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "칼로리",
                            value = todaySummary?.calories.toString() ?: "0",
                            unit = "kcal",
                            icon = Icons.Default.Calories,
                            iconColor = Color(0xFFFF6D00),
                            trend = "+8% vs 어제",
                            trendUp = true
                        ) {
                            if (secretMode) onSecretTrigger()
                        }
                    }
                }

                // Sleep card - full width
                item {
                    MetricCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "수면",
                        value = String.format("%.1f", todaySummary?.sleepHours ?: 0f),
                        unit = "시간",
                        icon = Icons.Default.Sleep,
                        iconColor = Color(0xFF7C4DFF),
                        trend = "목표: 8시간",
                        trendUp = (todaySummary?.sleepHours ?: 0f) >= 7.5f
                    ) {
                        if (secretMode) onSecretTrigger()
                    }
                }

                // Heart Rate card - full width with progress ring
                item {
                    ProgressRingCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "심박수",
                        progress = if (todaySummary != null && todaySummary.heartRateAvg > 0) {
                            (todaySummary.heartRateAvg.toFloat() / 200f).coerceIn(0f, 1f)
                        } else 0f,
                        value = todaySummary?.heartRateAvg.toString() ?: "--",
                        unit = "bpm",
                        icon = Icons.Default.HeartRate,
                        iconColor = Color(0xFFE91E63),
                        ringColor = Color(0xFFE91E63)
                    ) {
                        if (secretMode) onSecretTrigger()
                    }
                }

                // Weekly stats section
                item {
                    WeeklyStatsSection(stats = weeklyStats)
                }

                // 7-day chart preview
                item {
                    SevenDayChartSection(days = last7Days)
                }

                // Bottom spacing
                item {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Secret mode indicator (subtle)
    if (secretMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00C853).copy(alpha = 0.02f)),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = "🔓 시크릿 모드",
                fontSize = 10.sp,
                color = Color(0xFF00C853),
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    // PIN Dialog
    if (showPinDialog) {
        PinDialog(
            isSettingPin = isSettingPin,
            pinInput = pinInput,
            pinError = pinError,
            onPinChange = { pinInput = it },
            onSubmit = { pin ->
                if (isSettingPin) {
                    viewModel.setPin(pin)
                    showPinDialog = false
                    isSettingPin = false
                    pinInput = ""
                } else {
                    viewModel.verifyPin(pin).thenAccept { success ->
                        if (success) {
                            viewModel.toggleSecretMode()
                            showPinDialog = false
                            pinInput = ""
                            pinError = false
                        } else {
                            pinError = true
                        }
                    }
                }
            },
            onCancel = {
                showPinDialog = false
                pinInput = ""
                pinError = false
                isSettingPin = false
            }
        )
    }
}

@Composable
fun WeeklyStatsSection(stats: WeeklyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FakeHealthTheme.colorScheme.surface,
            contentColor = FakeHealthTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "이번 주 요약",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FakeHealthTheme.colorScheme.onSurface
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("총 걸음", "${formatNumber(stats.totalSteps)}보")
                StatItem("평균 걸음", "${formatNumber(stats.avgSteps)}보")
                StatItem("총 칼로리", "${formatNumber(stats.totalCalories)}kcal")
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("총 수면", String.format("%.1f시간", stats.totalSleepHours))
                StatItem("평균 수면", String.format("%.1f시간", stats.avgSleepHours))
                StatItem("평균 심박", "${stats.avgHeartRate}bpm")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FakeHealthTheme.colorScheme.onSurface)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SevenDayChartSection(days: List<DailySummary>) {
    if (days.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FakeHealthTheme.colorScheme.surface,
            contentColor = FakeHealthTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "최근 7일 걸음 수",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FakeHealthTheme.colorScheme.onSurface
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            // Simple bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.reversed().forEachIndexed { index, day ->
                    val maxSteps = days.maxOfOrNull { it.steps } ?: 10000
                    val height = if (maxSteps > 0) (day.steps.toFloat() / maxSteps * 100f) else 0f
                    Column(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height.dp)
                                .background(Color(0xFF0066FF))
                                .clip(RoundedCornerShape(4.dp))
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = SimpleDateFormat("E", Locale.getDefault()).format(Date(day.date)),
                            fontSize = 10.sp,
                            color = FakeHealthTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (day.steps > 0) formatNumber(day.steps) else "-",
                            fontSize = 9.sp,
                            color = FakeHealthTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinDialog(
    isSettingPin: Boolean,
    pinInput: String,
    pinError: Boolean,
    onPinChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    val dots = pinInput.padEnd(4, '•').take(4)

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = if (isSettingPin) "PIN 설정 (4자리)" : "PIN 입력",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                // PIN dots display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dots.forEach { char ->
                        Text(
                            text = char.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pinError) Color.Red else FakeHealthTheme.colorScheme.onSurface
                        )
                    }
                }
                if (pinError) {
                    Text(
                        text = "잘못된 PIN입니다",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(pinInput) }) {
                Text(text = if (isSettingPin) "설정" : "확인")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = "취소")
            }
        }
    )
}

fun formatNumber(num: Int): String {
    return if (num >= 10000) String.format("%.1f만", num / 10000f) else num.toString()
}