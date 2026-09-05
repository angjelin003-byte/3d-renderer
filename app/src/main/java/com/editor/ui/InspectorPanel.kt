package com.editor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InspectorPanel() {
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
