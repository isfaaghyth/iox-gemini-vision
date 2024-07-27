package utils.permission

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data object Denied : PermissionStatus()
    data object Rationale : PermissionStatus()
}
