package com.workreport.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object NewReport : Screen("new_report")
}
