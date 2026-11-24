package com.fakehealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fakehealth.ui.theme.FakeHealthTheme
import com.fakehealth.ui.theme.FakeHealthTypography
import com.fakehealth.ui.MainViewModel
import com.fakehealth.R

@Composable
fun PinSetupScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit
) {
    var pin1 by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(1) } // 1 = enter, 2 = confirm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FakeHealthTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.filled.Lock,
                contentDescription = null,
                tint = FakeHealthTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (step == 1) "비밀 PIN 설정" : "PIN 확인",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = FakeHealthTheme.colorScheme.onSurface
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (step == 1) "4자리 숫자를 입력하세요" : "다시 한 번 입력하세요",
            fontSize = 16.sp,
            color = FakeHealthTheme.colorScheme.onSurfaceVariant
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

        // PIN dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val currentPin = if (step == 1) pin1 else pin2
            (1..4).forEach { index ->
                val char = if (index <= currentPin.length) "●" else "○"
                val isError = error && currentPin.length == 4
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isError) Color.Red else FakeHealthTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (error) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PIN이 일치하지 않습니다",
                fontSize = 14.sp,
                color = Color.Red
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(48.dp))

        // Number pad
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberKey("1", onClick = { appendNumber("1") })
                NumberKey("2", onClick = { appendNumber("2") })
                NumberKey("3", onClick = { appendNumber("3") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberKey("4", onClick = { appendNumber("4") })
                NumberKey("5", onClick = { appendNumber("5") })
                NumberKey("6", onClick = { appendNumber("6") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberKey("7", onClick = { appendNumber("7") })
                NumberKey("8", onClick = { appendNumber("8") })
                NumberKey("9", onClick = { appendNumber("9") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberKey("", onClick = {}, enabled = false)
                NumberKey("0", onClick = { appendNumber("0") })
                NumberKey("⌫", onClick = { deleteNumber() }, isDelete = true)
            }
        }
    }

    fun appendNumber(num: String) {
        val currentPin = if (step == 1) pin1 else pin2
        if (currentPin.length < 4) {
            if (step == 1) pin1 = currentPin + num else pin2 = currentPin + num
            error = false

            // Auto-advance when 4 digits entered
            if (currentPin.length == 3) {
                if (step == 1) {
                    step = 2
                } else {
                    // Verify match
                    if (pin1 == pin2) {
                        viewModel.setPin(pin1)
                        onComplete()
                    } else {
                        error = true
                        pin2 = ""
                    }
                }
            }
        }
    }

    fun deleteNumber() {
        if (step == 1) {
            if (pin1.isNotEmpty()) pin1 = pin1.dropLast(1)
        } else {
            if (pin2.isNotEmpty()) pin2 = pin2.dropLast(1)
        }
        error = false
    }
}

@Composable
fun NumberKey(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isDelete: Boolean = false
) {
    val color = if (enabled) FakeHealthTheme.colorScheme.surface else FakeHealthTheme.colorScheme.surface.copy(alpha = 0.3f)
    val textColor = if (enabled) FakeHealthTheme.colorScheme.onSurface else FakeHealthTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    Card(
        modifier = Modifier.size(72.dp),
        onClick = if (enabled) onClick else null,
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(50.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isDelete) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.Backspace,
                    contentDescription = "지우기",
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = label,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}