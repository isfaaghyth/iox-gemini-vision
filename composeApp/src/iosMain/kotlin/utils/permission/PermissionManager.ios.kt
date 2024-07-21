package utils.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual class PermissionManager actual constructor(
    private val callback: PermissionCallback
) : PermissionHandler {
    
    @Composable
    override fun askPermission(permission: PermissionType) {
        when(permission) {
            is PermissionType.Camera -> {
                val status = remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
                askCameraPermission(status, permission, callback)
            }
            is PermissionType.None -> Unit
        }
    }

    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return if (permission == PermissionType.Camera) {
            val status: AVAuthorizationStatus =
                remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
            status == AVAuthorizationStatusAuthorized
        } else {
            false
        }
    }

    @Composable
    override fun launchSettings() {
        NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
            UIApplication.sharedApplication.openURL(it)
        }
    }

    private fun askCameraPermission(
        status: AVAuthorizationStatus, permission: PermissionType, callback: PermissionCallback
    ) {
        when (status) {
            AVAuthorizationStatusAuthorized -> {
                callback.onPermissionStatus(permission, PermissionStatus.Granted)
            }

            AVAuthorizationStatusNotDetermined -> {
                return AVCaptureDevice.Companion.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
                    if (isGranted) {
                        callback.onPermissionStatus(permission, PermissionStatus.Granted)
                    } else {
                        callback.onPermissionStatus(permission, PermissionStatus.Denied)
                    }
                }
            }

            AVAuthorizationStatusDenied -> {
                callback.onPermissionStatus(permission, PermissionStatus.Denied)
            }

            else -> error("unknown error: status $status")
        }
    }
}