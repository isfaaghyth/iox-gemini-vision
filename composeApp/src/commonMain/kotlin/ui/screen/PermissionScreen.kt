package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import utils.permission.PermissionCallback
import utils.permission.PermissionStatus
import utils.permission.PermissionType
import utils.permission.createPermissionsManager

@Composable
fun PermissionScreen(
    onCameraGranted: () -> Unit,
    shouldCloseApp: () -> Unit
) {
    var permissionStatus by remember { mutableStateOf<PermissionStatus>(PermissionStatus.Denied) }
    var isCameraPermissionGranted by remember { mutableStateOf(value = false) }
    
    val permissionManager = createPermissionsManager { _, status ->
        permissionStatus = status
        
        if (status == PermissionStatus.Granted) {
            isCameraPermissionGranted = true
        }
    }

    // check if the permission in system has been granted or not
    isCameraPermissionGranted = permissionManager.isPermissionGranted(PermissionType.Camera)
    
    // Actions
    var askPermission by rememberSaveable { mutableStateOf(false) }
    var launchSettings by rememberSaveable { mutableStateOf(false) }
    
    if (askPermission && !isCameraPermissionGranted) permissionManager.askPermission(PermissionType.Camera)
    if (launchSettings) permissionManager.launchSettings()

    // Init permission request
    LaunchedEffect(Unit) {
        askPermission = true
    }

    AnimatedContent(isCameraPermissionGranted) { isGranted ->
        if (isGranted) {
            onCameraGranted()
        } else {
            when (permissionStatus) {
                is PermissionStatus.Denied -> {
                    CameraPermission {
                        askPermission = !askPermission
                    }
                }
                is PermissionStatus.Rationale -> {
                    RationaleDenialDialog(
                        onOpenSettings = { launchSettings = true },
                        onDismiss = { shouldCloseApp() }
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
fun CameraPermission(
    onRequestPermission: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Gemini app need camera permission to continue.",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(Modifier.size(8.dp))
            Button(
                onClick = { onRequestPermission() }
            ) {
                Text("Request Permission")
            }

        }
    }
}

@Composable
fun RationaleDenialDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onOpenSettings()
                onDismiss()
            }) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(text = "Camera permission required") },
        text = { Text("Gemini app need camera permission to continue.") }
    )
}