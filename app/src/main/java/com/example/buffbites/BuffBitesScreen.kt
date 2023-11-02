package com.example.buffbites

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.buffbites.ui.OrderViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.buffbites.ui.StartOrderScreen
import com.example.buffbites.ui.ChooseMenuScreen
import com.example.buffbites.data.Datasource
import com.example.buffbites.ui.OrderSummaryScreen
import com.example.buffbites.ui.ChooseDeliveryTimeScreen
import javax.sql.DataSource

// TODO: Screen enum
enum class BuffBitesScreen() {
    Start,
    Meal,
    Delivery,
    Summary
}
// TODO: TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuffBitesApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // TODO: Add navigation dependency to gradle script
    // TODO: Create Controller and initialization

    Scaffold(
        topBar = { /* TODO */ }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = BuffBitesScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = BuffBitesScreen.Start.name) {
                StartOrderScreen(
                    restaurantOptions = Datasource.restaurants,
                    onNextButtonClicked = {
                        viewModel.updateVendor(it)
                        navController.navigate(BuffBitesScreen.Meal.name)
                    }
                )
            }
            composable(route = BuffBitesScreen.Meal.name) {
                val context = LocalContext.current
                ChooseMenuScreen(
                    options = uiState.selectedVendor?.menuItems,
                    onNextButtonClicked = { navController.navigate(BuffBitesScreen.Delivery.name) },
                    onCancelButtonClicked = { cancelOrder(viewModel, navController) },
                    onSelectionChanged = { viewModel.updateMeal(it) }
                )
            }
            composable(route = BuffBitesScreen.Delivery.name) {
                ChooseDeliveryTimeScreen(
                    subtotal = uiState.orderSubtotal,
                    onNextButtonClicked = { navController.navigate(BuffBitesScreen.Summary.name) },
                    onCancelButtonClicked = { cancelOrder(viewModel, navController) },
                    options = uiState.availableDeliveryTimes,
                    onSelectionChanged = { viewModel.updateDeliveryTime(it) }
                )
            }
            composable(route = BuffBitesScreen.Summary.name) {
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancelOrder(viewModel, navController ) },
                )
            }
        }
    }
}
private fun cancelOrder(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.navigate(BuffBitesScreen.Start.name)
}