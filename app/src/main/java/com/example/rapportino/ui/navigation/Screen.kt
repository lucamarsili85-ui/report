package com.example.rapportino.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddReport : Screen("add_report")
}
