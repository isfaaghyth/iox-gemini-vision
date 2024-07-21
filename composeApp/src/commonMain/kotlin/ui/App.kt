package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import di.Providers
import ui.screen.MainScreen
import ui.screen.PermissionScreen
import ui.uimodel.AppEvent

@Composable
fun GeminiApp(
    viewModel: AppViewModel = Providers.viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val state by viewModel.state.collectAsState()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Permission.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = AppRoute.Permission.name) {
                PermissionScreen(
                    onCameraGranted = {
                        navController.navigate(AppRoute.Main.name)
                    },
                    shouldCloseApp = { /* TODO */ }
                )
            }

            composable(route = AppRoute.Main.name) {
                MainScreen(
                    chats = state.chats
                ) { byteArray, prompt ->
                    val byteArrayImage = byteArray ?: return@MainScreen
                    viewModel.sendAction(
                        AppEvent.RequestWithAttachment(prompt, byteArrayImage)
                    )
                }
            }
        }
    }
}