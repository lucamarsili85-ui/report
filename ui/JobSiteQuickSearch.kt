package com.example.workreport.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * JobSiteQuickSearch demonstrates a quick search/filter UI for job sites.
 * 
 * This sample screen shows how to implement a text-based filter over a list
 * of job sites, allowing users to quickly find and select a job site.
 * 
 * @param onJobSiteSelected Callback when a job site is selected
 * @param onDismiss Callback to dismiss the search screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSiteQuickSearch(
    onJobSiteSelected: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    // Sample job site data - in a real app, this would come from a repository/ViewModel
    val allJobSites = remember {
        listOf(
            "Main Street Construction Site",
            "Highway 101 Bridge Repair",
            "Downtown Office Building",
            "City Park Renovation",
            "Industrial Complex Phase 2",
            "Residential Area Development",
            "Airport Expansion Project",
            "Waterfront Development",
            "Shopping Mall Construction",
            "Metro Station Alpha",
            "Metro Station Beta",
            "School Building Renovation",
            "Hospital Wing Addition",
            "Warehouse District Site 5",
            "Port Facility Upgrade"
        )
    }
    
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter job sites based on search query
    val filteredJobSites = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allJobSites
        } else {
            allJobSites.filter { 
                it.contains(searchQuery, ignoreCase = true) 
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Job Site") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search job sites...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Results count
            Text(
                text = if (searchQuery.isEmpty()) {
                    "All job sites (${allJobSites.size})"
                } else {
                    "Found ${filteredJobSites.size} of ${allJobSites.size} job sites"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Job sites list
            if (filteredJobSites.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Text(
                        text = "No job sites found matching \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredJobSites) { jobSite ->
                        JobSiteCard(
                            jobSite = jobSite,
                            searchQuery = searchQuery,
                            onClick = { onJobSiteSelected(jobSite) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card component for displaying a job site in the list.
 * 
 * @param jobSite The job site name
 * @param searchQuery The current search query for highlighting
 * @param onClick Callback when the card is clicked
 */
@Composable
private fun JobSiteCard(
    jobSite: String,
    searchQuery: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = jobSite,
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Show a hint about the search match if there's a query
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "Matches: \"$searchQuery\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
