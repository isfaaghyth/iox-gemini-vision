package utils.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

actual class PermissionManager actual constructor(
    private val callback: PermissionCallback
) : PermissionHandler {
    
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun askPermission(permission: PermissionType) {
        val lifecycleOwner = LocalLifecycleOwner.current
        
        if (permission == PermissionType.Camera) {
            val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
            
            LaunchedEffect(cameraPermissionState) {
                val permissionResult = cameraPermissionState.status
                if (!permissionResult.isGranted) {
                    if (permissionResult.shouldShowRationale) {
                        callback.onPermissionStatus(
                            permission, PermissionStatus.Rationale
                        )
                    } else {
                        lifecycleOwner.lifecycleScope.launch {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                } else {
                    callback.onPermissionStatus(
                        permission, PermissionStatus.Granted
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return if (permission == PermissionType.Camera) {
            val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
            cameraPermissionState.status.isGranted
        } else {
            false
        }
    }

    @Composable
    override fun launchSettings() {
        val context = LocalContext.current
        
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).also {
            context.startActivity(it)
        }
    }
}