package utils.permission

import androidx.compose.runtime.Composable

@Composable
fun createPermissionsManager(callback: PermissionCallback): PermissionManager {
    return PermissionManager(callback)
}

expect class PermissionManager(callback: PermissionCallback) : PermissionHandler

fun interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}