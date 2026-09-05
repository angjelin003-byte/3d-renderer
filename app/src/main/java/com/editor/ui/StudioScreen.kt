package com.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            // 1. Central Viewport Area hosting the 3D canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Viewport3DCanvas()
            }

            // 2. Sidebar Inspector Panel
            InspectorPanel()
        }
    }
}
