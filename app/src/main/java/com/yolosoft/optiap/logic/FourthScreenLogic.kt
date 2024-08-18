package com.yolosoft.optiap.logic

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

class FourthScreenViewModel : ViewModel() {
    var imageUri: Uri? by mutableStateOf(null)
    var strokeWidth by mutableFloatStateOf(5f)
    var strokeColor by mutableStateOf(Color.Black)
    var points: List<Offset> by mutableStateOf(emptyList())
}

class FourthScreen(private val context: Context) {
    @Composable
    fun Display(
        navigateToFifthScreen: () -> Unit,
        viewModelPrev: ThirdScreenViewModel = viewModel(),
        viewModelCurr: FourthScreenViewModel = viewModel()
    ) {
        // Transfer data from previous screen's ViewModel
        viewModelCurr.imageUri = viewModelPrev.imageUri
        viewModelCurr.strokeWidth = viewModelPrev.strokeWidth
        viewModelCurr.strokeColor = viewModelPrev.strokeColor
        viewModelCurr.points = viewModelPrev.points

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            viewModelPrev.imageUri?.let { imageUri ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val containerSize = size
                        val imageBounds = calculateImageBounds(context, containerSize, imageUri)

                        if (viewModelCurr.points.isNotEmpty()) {
                            val transformedPoints = viewModelCurr.points.map { point ->
                                Offset(
                                    x = imageBounds.left + (point.x),
                                    y = imageBounds.top + (point.y)
                                )
                            }

                            for (i in 0 until transformedPoints.size - 1) {
                                drawLine(
                                    color = viewModelCurr.strokeColor,
                                    start = transformedPoints[i],
                                    end = transformedPoints[i + 1],
                                    strokeWidth = viewModelCurr.strokeWidth
                                )
                            }
                            if (transformedPoints.size > 1) {
                                drawLine(
                                    color = viewModelCurr.strokeColor,
                                    start = transformedPoints.last(),
                                    end = transformedPoints.first(),
                                    strokeWidth = viewModelCurr.strokeWidth
                                )
                            }
                            transformedPoints.forEach { point ->
                                drawCircle(
                                    color = viewModelCurr.strokeColor,
                                    radius = 10f,
                                    center = point
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateImageBounds(context: Context, containerSize: Size, imageUri: Uri): Rect {
        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
        val image = ImageDecoder.decodeBitmap(source)
        val imageWidth = image.width.toFloat()
        val imageHeight = image.height.toFloat()

        val imageAspectRatio = imageWidth / imageHeight
        val containerAspectRatio = containerSize.width / containerSize.height

        return if (imageAspectRatio > containerAspectRatio) {
            // Image is wider than container
            val scaledHeight = containerSize.width / imageAspectRatio
            val top = (containerSize.height - scaledHeight) / 2
            Rect(0f, top, containerSize.width, top + scaledHeight)
        } else {
            // Image is taller than container
            val scaledWidth = containerSize.height * imageAspectRatio
            val left = (containerSize.width - scaledWidth) / 2
            Rect(left, 0f, left + scaledWidth, containerSize.height)
        }
    }
}