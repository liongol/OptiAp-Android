package com.yolosoft.optiap.logic

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

class SecondScreenViewModel : ViewModel() {
    var imageUri: Uri? by mutableStateOf(null)
}

class SecondScreen(private val context: Context) {
    @Composable
    fun Display(
        navigateToThirdScreen: () -> Unit,
        viewModelPrev: FirstScreenViewModel = viewModel(),
        viewModelCurr: SecondScreenViewModel = viewModel()
    ) {
        val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModelCurr.imageUri = uri
            }
        }

        LaunchedEffect(viewModelCurr.imageUri) {
            viewModelCurr.imageUri?.let {
                navigateToThirdScreen()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Button(
                onClick = {
                    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Upload map")
            }
            viewModelCurr.imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = androidx.compose.ui.graphics.Color.Red
                        )
                )
            }
        }
    }
}