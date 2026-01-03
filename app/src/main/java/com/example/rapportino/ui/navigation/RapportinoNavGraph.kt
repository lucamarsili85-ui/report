package com.example.rapportino.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rapportino.ui.addreport.AddReportScreen
import com.example.rapportino.ui.dashboard.DashboardScreen
import com.example.rapportino.viewmodel.AddReportViewModel
import com.example.rapportino.viewmodel.DashboardViewModel

@Composable
fun RapportinoNavGraph(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    addReportViewModel: AddReportViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onAddReportClick = {
                    navController.navigate(Screen.AddReport.route)
                }
            )
        }

        composable(Screen.AddReport.route) {
            AddReportScreen(
                viewModel = addReportViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
