package com.yolosoft.optiap.logic

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.yolosoft.optiap.R
import androidx.lifecycle.viewmodel.compose.viewModel

class ThirdScreenViewModel : ViewModel() {
    var imageUri: Uri? by mutableStateOf(null)
    val points: SnapshotStateList<Offset> = mutableStateListOf()
    val pointsRelativeLocation: SnapshotStateList<Offset> = mutableStateListOf()
    val redoPoints: SnapshotStateList<Offset> = mutableStateListOf()
    val redoPointsRelativeLocation: SnapshotStateList<Offset> = mutableStateListOf()
    var strokeWidth: Float by mutableFloatStateOf(5f)
    var strokeColor: Color by mutableStateOf(Color.Black)
}

class ThirdScreen(private val context: Context) {

    @Composable
    fun ColorPickerDialog(
        onColorSelected: (Color) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        val controller = rememberColorPickerController()

        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .background(Color.Black, shape = CircleShape)
                    .padding(16.dp)
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(10.dp),
                    controller = controller
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onColorSelected(controller.selectedColor.value)
                    onDismissRequest()
                }) {
                    Text("Select")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDismissRequest) {
                    Text("Close")
                }
            }
        }
    }

    @Composable
    fun ToolsDrawer(
        onUndoClick: () -> Unit,
        onRedoClick: () -> Unit,
        currentPencilColor: Color,
        onColorCircleClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onUndoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.undo_24px),
                    contentDescription = "Undo"
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(currentPencilColor, shape = CircleShape)
                    .clickable(onClick = onColorCircleClick)
                    .border(2.dp, Color.Gray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Pencil",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onRedoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.redo_24px),
                    contentDescription = "Redo"
                )
            }
        }
    }

    @Composable
    fun Display(
        navigateToFourthScreen: () -> Unit,
        viewModelPrev: SecondScreenViewModel = viewModel(),
        viewModelCurr: ThirdScreenViewModel = viewModel()
    ) {
        viewModelCurr.imageUri = viewModelPrev.imageUri

        var showColorPicker by remember { mutableStateOf(false) }
        var linesIntersect by remember { mutableStateOf(false) }

        LaunchedEffect(viewModelCurr.points.size, linesIntersect) {
            if (viewModelCurr.points.size >= 3 && !linesIntersect) {
                navigateToFourthScreen()
            }
        }

        if (showColorPicker) {
            ColorPickerDialog(
                onColorSelected = { color ->
                    viewModelCurr.strokeColor = color
                    showColorPicker = false
                },
                onDismissRequest = { showColorPicker = false }
            )
        }

        Column {
            ToolsDrawer(
                onUndoClick = {
                    if (viewModelCurr.points.isNotEmpty()) {
                        viewModelCurr.redoPoints.add(viewModelCurr.points.removeLast())
                        viewModelCurr.redoPointsRelativeLocation.add(viewModelCurr.pointsRelativeLocation.removeLast())
                    }
                },
                onRedoClick = {
                    if (viewModelCurr.redoPoints.isNotEmpty()) {
                        viewModelCurr.points.add(viewModelCurr.redoPoints.removeLast())
                        viewModelCurr.pointsRelativeLocation.add(viewModelCurr.redoPointsRelativeLocation.removeLast())
                    }
                },
                currentPencilColor = viewModelCurr.strokeColor,
                onColorCircleClick = { showColorPicker = true }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Border width:")
                Slider(
                    value = viewModelCurr.strokeWidth,
                    onValueChange = { viewModelCurr.strokeWidth = it },
                    valueRange = 1f..20f
                )
            }
            viewModelPrev.imageUri?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.Red
                            )
                            .pointerInput(Unit) {
                                detectTapGestures { offset: Offset ->
                                    val newPoint = Offset(offset.x, offset.y)
                                    val newPointsRelativeLocation = viewModelCurr.pointsRelativeLocation + listOf(newPoint)
                                    if (doLinesIntersect(newPointsRelativeLocation)) {
                                        linesIntersect = true
                                        Log.d("Image", "Lines intersect")
                                    }
                                    else {
                                        linesIntersect = false
                                        Log.d("Image", "Lines do not intersect")
                                    }
                                    viewModelCurr.points.add(newPoint)
                                    viewModelCurr.pointsRelativeLocation.add(newPoint)
                                    viewModelCurr.redoPoints.clear()
                                    Log.d("Image", "New point added: $newPoint")
                                }
                            }
                    )
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (viewModelCurr.points.isNotEmpty()) {
                            for (i in 0 until viewModelCurr.points.size - 1) {
                                drawLine(
                                    color = viewModelCurr.strokeColor,
                                    start = viewModelCurr.points[i],
                                    end = viewModelCurr.points[i + 1],
                                    strokeWidth = viewModelCurr.strokeWidth
                                )
                            }
                            if (viewModelCurr.points.size > 1) {
                                drawLine(
                                    color = viewModelCurr.strokeColor,
                                    start = viewModelCurr.points.last(),
                                    end = viewModelCurr.points.first(),
                                    strokeWidth = viewModelCurr.strokeWidth
                                )
                            }
                            viewModelCurr.points.forEach { point ->
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
            Button(
                onClick = {
                    navigateToFourthScreen()
                } ) {
                Text("Next")
            }
        }
    }

    private fun doLinesIntersect(points: List<Offset>): Boolean {
        val n = points.size
        for (i in 0 until n) {
            val p1 = points[i]
            val p2 = if (i == n - 1) points.first() else points[i + 1]
            for (j in i + 2 until n) {
                if (j == n - 1 && i == 0) continue // Skip adjacent lines
                val p3 = points[j]
                val p4 = if (j == n - 1) points.first() else points[j + 1]
                if (linesIntersect(p1, p2, p3, p4)) {
                    return true
                }
            }
        }
        return false
    }

    private fun linesIntersect(p1: Offset, p2: Offset, p3: Offset, p4: Offset): Boolean {
        val d1 = direction(p3, p4, p1)
        val d2 = direction(p3, p4, p2)
        val d3 = direction(p1, p2, p3)
        val d4 = direction(p1, p2, p4)

        if (d1 != d2 && d3 != d4) {
            return true
        }

        if (d1 == 0 && onSegment(p3, p4, p1)) return true
        if (d2 == 0 && onSegment(p3, p4, p2)) return true
        if (d3 == 0 && onSegment(p1, p2, p3)) return true
        if (d4 == 0 && onSegment(p1, p2, p4)) return true

        return false
    }

    private fun direction(p1: Offset, p2: Offset, p3: Offset): Int {
        val value = (p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y)
        return when {
            value > 0 -> 1
            value < 0 -> -1
            else -> 0
        }
    }

    private fun onSegment(p1: Offset, p2: Offset, p: Offset): Boolean {
        return p.x >= minOf(p1.x, p2.x) && p.x <= maxOf(p1.x, p2.x) && p.y >= minOf(p1.y, p2.y) && p.y <= maxOf(p1.y, p2.y)
    }
}