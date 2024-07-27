package utils.permission

sealed class PermissionType {
    data object Camera : PermissionType()
    data object Audio : PermissionType()
}
