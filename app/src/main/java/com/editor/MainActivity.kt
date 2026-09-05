package com.editor

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

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
    // UI State: Holds names of objects in the scene
    val sceneHierarchy = remember { mutableStateListOf<String>() }
    
    // References to OpenGL components
    var glSurfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }
    val renderer = remember { C4DViewportRenderer() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF2B2B2B))) {
        // Top Menu Bar
        Row(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF3C3C3C))) {
            TextButton(onClick = { }) { Text("File", color = Color.White) }
            TextButton(onClick = { }) { Text("View", color = Color.White) }
            
            // Add Object Button (Linked to OpenGL thread)
            TextButton(onClick = {
                glSurfaceView?.queueEvent {
                    val objName = "Cube ${sceneHierarchy.size + 1}"
                    renderer.addCube(objName)
                    
                    // Post back to main thread to update Compose UI
                    glSurfaceView?.post { sceneHierarchy.add(objName) }
                    
                    // Force OpenGL to draw the new frame
                    glSurfaceView?.requestRender()
                }
            }) { Text("Add Cube", color = Color.Green) }
            
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { }) { Text("Undo", color = Color.White) }
            TextButton(onClick = { }) { Text("Redo", color = Color.White) }
        }

        Row(modifier = Modifier.weight(1f)) {
            // Left Toolbar: Shortcuts
            Column(modifier = Modifier.width(50.dp).fillMaxHeight().background(Color(0xFF323232))) {
                Button(onClick = { }, modifier = Modifier.padding(top = 8.dp)) { Text("P") }
                Button(onClick = { }) { Text("L") }
                Button(onClick = { }) { Text("F") }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { }) { Text("Cam") }
            }

            // Center Viewport (OpenGL ES)
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                AndroidView(
                    factory = { context ->
                        GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)
                            setRenderer(renderer)
                            // RENDERMODE_WHEN_DIRTY saves mobile battery by only drawing on camera move or object add
                            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY 
                            setOnTouchListener(CameraTouchController(renderer.camera))
                            glSurfaceView = this // Save reference for button clicks
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right Panels: Hierarchy & Attributes
            Column(modifier = Modifier.width(200.dp).fillMaxHeight().background(Color(0xFF3C3C3C))) {
                
                // Scene Graph
                Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
                    Text("Objects", color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Dynamic List of Objects
                    LazyColumn {
                        items(sceneHierarchy) { objName ->
                            Text(
                                text = objName,
                                color = Color.LightGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clickable { /* Select object in renderer */ }
                            )
                        }
                    }
                }
                
                Divider(color = Color.DarkGray)
                
                // Attributes Panel
                Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
                    Text("Attributes", color = Color.White)
                    // Transform, Scale, Rotation modifiers go here
                }
            }
        }

        // Bottom Panel: Material Editor
        Row(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFF323232))) {
            Text("Material Editor", color = Color.LightGray, modifier = Modifier.padding(8.dp))
        }
    }
}
