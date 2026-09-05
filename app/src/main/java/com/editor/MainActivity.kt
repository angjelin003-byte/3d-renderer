package com.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.editor.ui.theme.EditorTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable immersive fullscreen mode (hides status & navigation bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            EditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudioScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("3D Editor Studio") },
                actions = {
                    Button(
                        onClick = { /* TODO: Trigger actions or export */ },
                        modifier = Modifier.padding(end = 8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Export")
                    }
                }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Interactive 3D Perspective Viewport Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Viewport3DCanvas()
            }

            // 2. Sidebar Inspector Panel
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Inspector",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    HorizontalDivider()

                    Text(
                        text = "Transform Position",
                        style = MaterialTheme.typography.labelLarge
                    )

                    OutlinedTextField(
                        value = "0.0",
                        onValueChange = {},
                        label = { Text("Position X") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = "0.0",
                        onValueChange = {},
                        label = { Text("Position Y") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = "0.0",
                        onValueChange = {},
                        label = { Text("Position Z") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

data class Point3D(val x: Float, val y: Float, val z: Float)

@Composable
fun Viewport3DCanvas() {
    var rotationX by remember { mutableStateOf(25f) }
    var rotationY by remember { mutableStateOf(45f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotationY += dragAmount.x * 0.6f
                    rotationX -= dragAmount.y * 0.6f
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val scaleFactor = 120f

            fun project(p: Point3D): Offset {
                val radX = Math.toRadians(rotationX.toDouble()).toFloat()
                val radY = Math.toRadians(rotationY.toDouble()).toFloat()

                // Rotate around Y axis
                val x1 = p.x * cos(radY) - p.z * sin(radY)
                val z1 = p.x * sin(radY) + p.z * cos(radY)
                val y1 = p.y

                // Rotate around X axis
                val y2 = y1 * cos(radX) - z1 * sin(radX)
                val z2 = y1 * sin(radX) + z1 * cos(radX)

                // Perspective projection depth
                val distance = 4f
                val perspective = distance / (distance + z2)

                return Offset(
                    cx + x1 * scaleFactor * perspective,
                    cy + y2 * scaleFactor * perspective
                )
            }

            // Define a 3D Cube model vertices
            val cubeVertices = listOf(
                Point3D(-1f, -1f, -1f), Point3D(1f, -1f, -1f),
                Point3D(1f, 1f, -1f), Point3D(-1f, 1f, -1f),
                Point3D(-1f, -1f, 1f), Point3D(1f, -1f, 1f),
                Point3D(1f, 1f, 1f), Point3D(-1f, 1f, 1f)
            )

            // Cube edges connection map
            val edges = listOf(
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0), // Back face
                Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4), // Front face
                Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7)  // Connecting edges
            )

            val projectedVertices = cubeVertices.map { project(it) }

            // Draw Cube Edges
            edges.forEach { (start, end) ->
                drawLine(
                    color = Color(0xFF6200EE),
                    start = projectedVertices[start],
                    end = projectedVertices[end],
                    strokeWidth = 4f
                )
            }

            // Draw vertices points
            projectedVertices.forEach { pt ->
                drawCircle(color = Color.White, radius = 6f, center = pt)
            }
        }

        // Helper label overlay
        Text(
            text = "Interactive 3D Viewport • Drag to Orbit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        )
    }
}
