package ui

sealed class AppRoute(val name: String) {
    data object Permission : AppRoute("permission")
    data object Main : AppRoute("main")
}