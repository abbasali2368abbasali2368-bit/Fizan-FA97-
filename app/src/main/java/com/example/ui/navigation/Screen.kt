package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Chat : Screen("chat/{chatId}/{peerCode}") {
        fun createRoute(chatId: String, peerCode: String) = "chat/$chatId/$peerCode"
    }
}
