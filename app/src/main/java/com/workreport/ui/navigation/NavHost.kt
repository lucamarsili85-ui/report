package com.workreport.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.workreport.ui.screens.DashboardScreen
import com.workreport.ui.screens.NewReportScreen
import com.workreport.viewmodel.WorkReportViewModel

@Composable
fun WorkReportNavHost(
    navController: NavHostController,
    viewModel: WorkReportViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToNewReport = {
                    navController.navigate(Screen.NewReport.route)
                },
                viewModel = viewModel
            )
        }
        composable(Screen.NewReport.route) {
            NewReportScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}
