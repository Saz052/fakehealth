package com.fakehealth.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.fakehealth.ui.screens.DashboardScreen
import com.fakehealth.ui.screens.PinSetupScreen
import com.fakehealth.ui.screens.SecretEditorScreen
import com.fakehealth.ui.theme.FakeHealthTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FakeHealthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FakeHealthTheme.colorScheme.background
                ) {
                    AppScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppScreen(viewModel: MainViewModel) {
    var screen by remember { mutableStateOf<Screen>(Screen.DASHBOARD) }
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }
    val hasPin by viewModel.hasPin.observeAsState(false)
    val secretMode by viewModel.secretModeEnabled.observeAsState(false)

    enum class Screen {
        DASHBOARD, PIN_SETUP, PIN_ENTRY, SECRET_EDITOR
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val now = System.currentTimeMillis()
                        if (now - lastTapTime < 1500) {
                            tapCount++
                        } else {
                            tapCount = 1
                        }
                        lastTapTime = now

                        if (tapCount >= 5) {
                            tapCount = 0
                            if (!hasPin) {
                                screen = Screen.PIN_SETUP
                            } else if (!secretMode) {
                                screen = Screen.PIN_ENTRY
                            } else {
                                screen = Screen.SECRET_EDITOR
                            }
                        }
                    }
                )
            }
    ) {
        when (screen) {
            Screen.DASHBOARD -> DashboardScreen(
                viewModel = viewModel,
                onSecretTrigger = {
                    if (secretMode) {
                        screen = Screen.SECRET_EDITOR
                    }
                }
            )
            Screen.PIN_SETUP -> PinSetupScreen(
                viewModel = viewModel,
                onComplete = { screen = Screen.DASHBOARD }
            )
            Screen.PIN_ENTRY -> PinEntryScreen(
                viewModel = viewModel,
                onSuccess = { screen = Screen.SECRET_EDITOR },
                onCancel = { screen = Screen.DASHBOARD }
            )
            Screen.SECRET_EDITOR -> SecretEditorScreen(
                viewModel = viewModel,
                onClose = { screen = Screen.DASHBOARD }
            )
        }
    }
}

@Composable
fun PinEntryScreen(
    viewModel: MainViewModel,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.filled.Lock,
                contentDescription = null,
                tint = FakeHealthTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "시크릿 모드 진입",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = FakeHealthTheme.colorScheme.onSurface
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "4자리 PIN을 입력하세요",
            fontSize = 16.sp,
            color = FakeHealthTheme.colorScheme.onSurfaceVariant
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

        // PIN dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            (1..4).forEach { index ->
                val char = if (index <= pin.length) "●" else "○"
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        fontSize = 32.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = if (error) Color.Red else FakeHealthTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (error) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "잘못된 PIN입니다",
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
                PinNumberKey("1", onClick = { append("1") })
                PinNumberKey("2", onClick = { append("2") })
                PinNumberKey("3", onClick = { append("3") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PinNumberKey("4", onClick = { append("4") })
                PinNumberKey("5", onClick = { append("5") })
                PinNumberKey("6", onClick = { append("6") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PinNumberKey("7", onClick = { append("7") })
                PinNumberKey("8", onClick = { append("8") })
                PinNumberKey("9", onClick = { append("9") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PinNumberKey("", onClick = {}, enabled = false)
                PinNumberKey("0", onClick = { append("0") })
                PinNumberKey("⌫", onClick = { delete() }, isDelete = true)
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.material3.TextButton(onClick = onCancel) {
            Text(text = "취소", fontSize = 16.sp, color = FakeHealthTheme.colorScheme.onSurfaceVariant)
        }
    }

    fun append(num: String) {
        if (pin.length < 4) {
            pin += num
            error = false
            if (pin.length == 4) {
                verifyPin()
            }
        }
    }

    fun delete() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            error = false
        }
    }

    fun verifyPin() {
        viewModel.verifyPin(pin).thenAccept { success ->
            if (success) {
                onSuccess()
            } else {
                error = true
                pin = ""
            }
        }
    }
}

@Composable
fun PinNumberKey(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isDelete: Boolean = false
) {
    val color = if (enabled) FakeHealthTheme.colorScheme.surface else FakeHealthTheme.colorScheme.surface.copy(alpha = 0.3f)
    val textColor = if (enabled) FakeHealthTheme.colorScheme.onSurface else FakeHealthTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    androidx.compose.material3.Card(
        modifier = Modifier.size(72.dp),
        onClick = if (enabled) onClick else null,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = color,
            contentColor = textColor
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isDelete) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.filled.Backspace,
                    contentDescription = "지우기",
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = label,
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}