package com.yolosoft.optiap.logic

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class FirstScreenViewModel : ViewModel() {

}

class FirstScreen(private val context: Context) {
    @Composable
    fun Display(
        viewModelCurr: FirstScreenViewModel = viewModel()
    ) {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Center the content vertically
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                        slideInVertically(initialOffsetY = { -40 }, animationSpec = tween(durationMillis = 1000))
            ) {
                BasicText(
                    text = "Welcome to OptiAP - your premier tool for optimizing WiFi performance",
                    style = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            ) {
                Text(
                    text = "To learn how to use this application, please access the menu by pressing the hamburger icon or swiping from the left.",
                    style = LocalTextStyle.current.copy(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "If you are already familiar with the application, proceed by pressing the arrow located at the bottom right corner of the screen.",
                    style = LocalTextStyle.current.copy(fontSize = 16.sp)
                )
            }
        }
    }
}