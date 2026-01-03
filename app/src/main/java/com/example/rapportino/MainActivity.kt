package com.example.rapportino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.rapportino.ui.navigation.RapportinoNavGraph
import com.example.rapportino.ui.theme.RapportinoTheme
import com.example.rapportino.viewmodel.AddReportViewModel
import com.example.rapportino.viewmodel.AddReportViewModelFactory
import com.example.rapportino.viewmodel.DashboardViewModel
import com.example.rapportino.viewmodel.DashboardViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as RapportinoApplication
        val repository = application.repository

        setContent {
            RapportinoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    val dashboardViewModel: DashboardViewModel = viewModel(
                        factory = DashboardViewModelFactory(repository)
                    )
                    
                    val addReportViewModel: AddReportViewModel = viewModel(
                        factory = AddReportViewModelFactory(repository)
                    )

                    RapportinoNavGraph(
                        navController = navController,
                        dashboardViewModel = dashboardViewModel,
                        addReportViewModel = addReportViewModel
                    )
                }
            }
        }
    }
}
