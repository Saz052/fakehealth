package com.fakehealth.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.fakehealth.ui.theme.FakeHealthTheme
import com.fakehealth.ui.theme.FakeHealthTypography
import com.fakehealth.ui.MainViewModel
import com.fakehealth.ui.components.Icons
import com.fakehealth.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SecretEditorScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var steps by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var sleepHours by remember { mutableStateOf("") }
    var sleepMinutes by remember { mutableStateOf("") }
    var heartRateAvg by remember { mutableStateOf("") }
    var heartRateMin by remember { mutableStateOf("") }
    var heartRateMax by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Load current data for selected date
    // In a real app, you'd observe the flow for the selected date

    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.getDefault())
    val shortDateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().background(FakeHealthTheme.colorScheme.background)) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = "🔧 데이터 편집기",
                    fontSize = 20.sp,
                    fontWeight = FakeHealthTypography.titleLarge.fontWeight,
                    color = FakeHealthTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = FakeHealthTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = FakeHealthTheme.colorScheme.surface,
                titleContentColor = FakeHealthTheme.colorScheme.onSurface
            )
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

        // Date selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = FakeHealthTheme.colorScheme.surface,
                contentColor = FakeHealthTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "편집 날짜",
                    fontSize = 16.sp,
                    fontWeight = FakeHealthTypography.titleMedium.fontWeight,
                    color = FakeHealthTheme.colorScheme.onSurface
                )
                Button(onClick = { showDatePicker = true }) {
                    Text(
                        text = dateFormat.format(Date(selectedDate)),
                        fontSize = 14.sp,
                        color = FakeHealthTheme.colorScheme.primary
                    )
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

        // Input fields
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                InputField(
                    label = "걸음 수",
                    value = steps,
                    onValueChange = { steps = it },
                    icon = Icons.Default.Steps,
                    iconColor = Color(0xFF0066FF),
                    keyboardType = KeyboardType.Number
                )
            }
            item {
                InputField(
                    label = "칼로리 (kcal)",
                    value = calories,
                    onValueChange = { calories = it },
                    icon = Icons.Default.Calories,
                    iconColor = Color(0xFFFF6D00),
                    keyboardType = KeyboardType.Number
                )
            }
            item {
                SleepInputField(
                    label = "수면 시간",
                    hours = sleepHours,
                    minutes = sleepMinutes,
                    onHoursChange = { sleepHours = it },
                    onMinutesChange = { sleepMinutes = it },
                    icon = Icons.Default.Sleep,
                    iconColor = Color(0xFF7C4DFF)
                )
            }
            item {
                HeartRateInputField(
                    label = "심박수 (bpm)",
                    avg = heartRateAvg,
                    min = heartRateMin,
                    max = heartRateMax,
                    onAvgChange = { heartRateAvg = it },
                    onMinChange = { heartRateMin = it },
                    onMaxChange = { heartRateMax = it },
                    icon = Icons.Default.HeartRate,
                    iconColor = Color(0xFFE91E63)
                )
            }
        }

        // Save button at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(FakeHealthTheme.colorScheme.background)
        ) {
            Button(
                onClick = {
                    saveData()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FakeHealthTheme.colorScheme.primary,
                    contentColor = FakeHealthTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "저장하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialogWrapper(
            initialDate = selectedDate,
            onDateSet = { year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                selectedDate = cal.timeInMillis
                showDatePicker = false
                // Reset fields for new date
                steps = ""
                calories = ""
                sleepHours = ""
                sleepMinutes = ""
                heartRateAvg = ""
                heartRateMin = ""
                heartRateMax = ""
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Success toast
    if (showSuccess) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.padding(bottom = 100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00C853),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = successMessage,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    fun saveData() {
        val s = steps.toIntOrNull() ?: 0
        val c = calories.toIntOrNull() ?: 0
        val sh = sleepHours.toIntOrNull() ?: 0
        val sm = sleepMinutes.toIntOrNull() ?: 0
        val hrAvg = heartRateAvg.toIntOrNull() ?: 0
        val hrMin = heartRateMin.toIntOrNull() ?: 0
        val hrMax = heartRateMax.toIntOrNull() ?: 0

        val totalSleepMinutes = sh * 60 + sm

        viewModel.updateRecordForDate(
            date = selectedDate,
            steps = s,
            calories = c,
            sleepMinutes = totalSleepMinutes,
            heartRateAvg = hrAvg,
            heartRateMin = hrMin,
            heartRateMax = hrMax
        )

        successMessage = "저장 완료: ${shortDateFormat.format(Date(selectedDate))} - ${formatNumber(s)}보, ${c}kcal, ${sh}h ${sm}m, 심박 ${hrAvg}/${hrMin}-${hrMax}"
        showSuccess = true

        // Auto dismiss after 2 seconds
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            showSuccess = false
        }, 2000)
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FakeHealthTheme.colorScheme.surface,
            contentColor = FakeHealthTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp).padding(end = 16.dp)
            )
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = label) },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier
                    .fillMaxWidth()
                    .width(0.dp), // weight 1
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = FakeHealthTheme.colorScheme.surface,
                    focusedContainerColor = FakeHealthTheme.colorScheme.surface,
                    textColor = FakeHealthTheme.colorScheme.onSurface,
                    labelColor = FakeHealthTheme.colorScheme.onSurfaceVariant,
                    cursorColor = FakeHealthTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun SleepInputField(
    label: String,
    hours: String,
    minutes: String,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FakeHealthTheme.colorScheme.surface,
            contentColor = FakeHealthTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp).padding(end = 16.dp)
            )
            Text(text = label, fontSize = 16.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 16.dp))
            TextField(
                value = hours,
                onValueChange = onHoursChange,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(80.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = FakeHealthTheme.colorScheme.surface,
                    textColor = FakeHealthTheme.colorScheme.onSurface
                )
            )
            Text(text = "시간", fontSize = 14.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp))
            TextField(
                value = minutes,
                onValueChange = onMinutesChange,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(80.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = FakeHealthTheme.colorScheme.surface,
                    textColor = FakeHealthTheme.colorScheme.onSurface
                )
            )
            Text(text = "분", fontSize = 14.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HeartRateInputField(
    label: String,
    avg: String,
    min: String,
    max: String,
    onAvgChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FakeHealthTheme.colorScheme.surface,
            contentColor = FakeHealthTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp).padding(end = 16.dp)
                )
                Text(text = label, fontSize = 16.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant)
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeartRateField("평균", avg, onAvgChange)
                HeartRateField("최저", min, onMinChange)
                HeartRateField("최고", max, onMaxChange)
            }
        }
    }
}

@Composable
fun HeartRateField(label: String, value: String, onChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onChange,
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(80.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = FakeHealthTheme.colorScheme.surface,
                textColor = FakeHealthTheme.colorScheme.onSurface
            )
        )
        Text(text = "bpm", fontSize = 10.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun DatePickerDialogWrapper(
    initialDate: Long,
    onDateSet: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cal = Calendar.getInstance()
    cal.timeInMillis = initialDate

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "날짜 선택", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            androidx.compose.foundation.layout.Box(modifier = Modifier.height(300.dp)) {
                android.widget.DatePicker(context).apply {
                    init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)) { _, year, month, day ->
                        onDateSet(year, month, day)
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("확인") } }
    )
}

fun formatNumber(num: Int): String {
    return if (num >= 10000) String.format("%.1f만", num / 10000f) else num.toString()
}