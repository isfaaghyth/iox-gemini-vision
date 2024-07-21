package utils.permission

import androidx.compose.runtime.Composable

@Composable
fun createPermissionsManager(callback: PermissionCallback): PermissionManager {
    return PermissionManager(callback)
}

expect class PermissionManager(callback: PermissionCallback) : PermissionHandler

sealed class PermissionType {
    data object Camera : PermissionType()
    data object None : PermissionType()
}

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data object Denied : PermissionStatus()
    data object Rationale : PermissionStatus()
}

interface PermissionHandler {
    @Composable fun askPermission(permission: PermissionType)
    @Composable fun isPermissionGranted(permission: PermissionType): Boolean
    @Composable fun launchSettings()
}

fun interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}