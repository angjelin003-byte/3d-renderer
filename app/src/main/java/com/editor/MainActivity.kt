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
    val sceneHierarchy = remember { mutableStateListOf<String>() }
    var glSurfaceView by remember { mutableStateOf<GLSurfaceView?>(null) }
    val renderer = remember { C4DViewportRenderer() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF2B2B2B))) {
        Row(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF3C3C3C))) {
            TextButton(onClick = { }) { Text("File", color = Color.White) }
            TextButton(onClick = { }) { Text("View", color = Color.White) }
            
            TextButton(onClick = {
                glSurfaceView?.queueEvent {
                    val objName = "Cube ${sceneHierarchy.size + 1}"
                    renderer.addCube(objName)
                    
                    glSurfaceView?.post { sceneHierarchy.add(objName) }
                    glSurfaceView?.requestRender()
                }
            }) { Text("Add Cube", color = Color.Green) }
            
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { }) { Text("Undo", color = Color.White) }
            TextButton(onClick = { }) { Text("Redo", color = Color.White) }
        }

        Row(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.width(50.dp).fillMaxHeight().background(Color(0xFF323232))) {
                Button(onClick = { }, modifier = Modifier.padding(top = 8.dp)) { Text("P") }
                Button(onClick = { }) { Text("L") }
                Button(onClick = { }) { Text("F") }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { }) { Text("Cam") }
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                AndroidView(
                    factory = { context ->
                        GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)
                            setRenderer(renderer)
                            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY 
                            setOnTouchListener(CameraTouchController(renderer, this))
                            glSurfaceView = this 
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.width(200.dp).fillMaxHeight().background(Color(0xFF3C3C3C))) {
                Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
                    Text("Objects", color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LazyColumn {
                        items(sceneHierarchy) { objName ->
                            Text(
                                text = objName,
                                color = Color.LightGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clickable { }
                            )
                        }
                    }
                }
                
                Divider(color = Color.DarkGray)
                
                Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
                    Text("Attributes", color = Color.White)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFF323232))) {
            Text("Material Editor", color = Color.LightGray, modifier = Modifier.padding(8.dp))
        }
    }
}
