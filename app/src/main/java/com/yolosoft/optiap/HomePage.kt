package com.yolosoft.optiap

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yolosoft.optiap.logic.FirstScreen
import com.yolosoft.optiap.logic.FirstScreenViewModel
import com.yolosoft.optiap.logic.FourthScreen
import com.yolosoft.optiap.logic.FourthScreenViewModel
import com.yolosoft.optiap.logic.SecondScreen
import com.yolosoft.optiap.logic.SecondScreenViewModel
import com.yolosoft.optiap.logic.ThirdScreen
import com.yolosoft.optiap.logic.ThirdScreenViewModel

// private const val WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED"

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableIntStateOf(0) }
    var previousScreen by remember { mutableIntStateOf(0) }
    var isNextButtonEnabled by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<String?>(null) }
    val animationDuration = 750
    val context = LocalContext.current
    val firstScreenViewModel: FirstScreenViewModel = viewModel() // storing data in first screen, using it in second screen
    val secondScreenViewModel: SecondScreenViewModel = viewModel() // storing data in second screen, using it in third screen
    val thirdScreenViewModel: ThirdScreenViewModel = viewModel() // storing data in third screen, using it in fourth screen
    val fourthScreenViewModel: FourthScreenViewModel = viewModel() // storing data in fourth screen, using it in fifth screen

    fun resetNextButtonState() {
        isNextButtonEnabled = false
    }

    val pages = listOf<@Composable () -> Unit>(
        { FirstScreen(context).Display(
            viewModelCurr = firstScreenViewModel
        ) },
        { SecondScreen(context).Display(
            navigateToThirdScreen = { isNextButtonEnabled = true },
            viewModelPrev = firstScreenViewModel,
            viewModelCurr = secondScreenViewModel,
        ) },
        { ThirdScreen(context).Display(
            navigateToFourthScreen = { isNextButtonEnabled = true },
            viewModelPrev = secondScreenViewModel,
            viewModelCurr = thirdScreenViewModel
        ) },
        { FourthScreen(context).Display(
            navigateToFifthScreen = { isNextButtonEnabled = true },
            viewModelPrev = thirdScreenViewModel,
            viewModelCurr = fourthScreenViewModel
        ) }
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.fillMaxSize()) {
                pages.forEachIndexed { index, page ->
                    androidx.compose.animation.AnimatedVisibility(
                        visible = currentScreen == index,
                        enter = slideInHorizontally(
                            initialOffsetX = { if (previousScreen < currentScreen) it else -it },
                            animationSpec = tween(durationMillis = animationDuration)
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { if (previousScreen < currentScreen) -it else it },
                            animationSpec = tween(durationMillis = animationDuration)
                        )
                    ) {
                        page()
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentScreen > 0) {
                IconButton(
                    onClick = {
                        if (currentScreen > 0) {
                            if (currentScreen == 1) {
                                resetNextButtonState()
                            }
                            previousScreen = currentScreen
                            currentScreen--
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
            if (currentScreen < pages.size - 1) {
                IconButton(
                    onClick = {
                        if (currentScreen < pages.size - 1) {
                            if (currentScreen == 1) {
                                resetNextButtonState()
                            }
                            previousScreen = currentScreen
                            currentScreen++
                        }
                    },
                    enabled = currentScreen == 0 || isNextButtonEnabled,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (currentScreen < pages.size - 1 && (currentScreen == 0 || isNextButtonEnabled))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = if (currentScreen < pages.size - 1 && (currentScreen == 0 || isNextButtonEnabled))
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
        }
    }
}

//fun checkHotspotStatus(context: Context): Boolean {
//    return try {
//        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
//        method.isAccessible = true
//        val result = method.invoke(wifiManager) as Boolean
//        Log.i("checkHotspotStatus", "Hotspot status: $result")
//        result
//    } catch (e: Exception) {
//        Log.e("checkHotspotStatus", "Error checking hotspot status", e)
//        false
//    }
//}
//
//fun setHotspotEnabled(context: Context, enabled: Boolean) {
//    try {
//        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val method = wifiManager.javaClass.getDeclaredMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
//        method.isAccessible = true
//        method.invoke(wifiManager, null, enabled)
//    } catch (e: Exception) {
//        Log.e("setHotspotEnabled", "Error setting hotspot state", e)
//    }
//}
//
//fun openHotspotSettings(context: Context) {
//    val intent = Intent(Intent.ACTION_MAIN)
//    intent.setClassName("com.android.settings", "com.android.settings.TetherSettings")
//    context.startActivity(intent)
//}