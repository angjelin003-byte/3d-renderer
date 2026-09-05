package com.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.opengl.GLSurfaceView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Cinema4DLayout()
            }
        }
    }
}

@Composable
fun Cinema4DLayout() {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF2B2B2B))) {
        // Top Menu Bar
        Row(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF3C3C3C))) {
            TextButton(onClick = { /* Open, New, Save */ }) { Text("File", color = Color.White) }
            TextButton(onClick = { /* Prespective, Orthogonal, Wireframe, Rendered, Lights */ }) { Text("View", color = Color.White) }
            TextButton(onClick = { /* Cube, Sphere, Plane, Torus */ }) { Text("Add Object", color = Color.White) }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { /* Undo */ }) { Text("Undo", color = Color.White) }
            TextButton(onClick = { /* Redo */ }) { Text("Redo", color = Color.White) }
        }

        Row(modifier = Modifier.weight(1f)) {
            // Left Toolbar: Shortcuts (C4D Style)
            Column(modifier = Modifier.width(50.dp).fillMaxHeight().background(Color(0xFF323232))) {
                Button(onClick = { /* Points */ }) { Text("P") }
                Button(onClick = { /* Lines/Edges */ }) { Text("L") }
                Button(onClick = { /* Faces */ }) { Text("F") }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { /* Camera Movement Toggle */ }) { Text("Cam") }
            }

            // Center Viewport
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                AndroidView(
                    factory = { context ->
                        GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)
                            setRenderer(C4DViewportRenderer())
                            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right Panels: Hierarchy & Attributes
            Column(modifier = Modifier.width(200.dp).fillMaxHeight().background(Color(0xFF3C3C3C))) {
                // Hierarchy (Scene Graph)
                Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(4.dp)) {
                    Text("Objects", color = Color.LightGray)
                    // List of added objects goes here
                }
                Divider(color = Color.DarkGray)
                // Attributes
                Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(4.dp)) {
                    Text("Attributes", color = Color.LightGray)
                    // Transform, Scale, Rotation sliders go here
                }
            }
        }

        // Bottom Panel: Material Editor
        Row(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFF323232))) {
            Text("Material Editor", color = Color.LightGray, modifier = Modifier.padding(8.dp))
            // Material preview spheres go here
        }
    }
}
