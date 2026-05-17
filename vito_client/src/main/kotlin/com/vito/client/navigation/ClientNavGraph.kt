package com.vito.client.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vito.client.ui.auth.LoginScreen
import com.vito.client.ui.home.HomeScreen
import com.vito.client.ui.splash.SplashScreen

/**
 * Vito Client Navigation - type-safe routes per PLAN.md §23
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Otp : Screen("otp/{phone}") {
        fun createRoute(phone: String) = "otp/$phone"
    }
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object RideBooking : Screen("ride_booking")
    data object ActiveRide : Screen("active_ride/{jobId}") {
        fun createRoute(jobId: String) = "active_ride/$jobId"
    }
    data object PlacePicker : Screen("place_picker")
    data object SendBooking : Screen("send_booking")
    data object Mart : Screen("mart")
    data object MartCart : Screen("mart_cart")
    data object Wallet : Screen("wallet")
    data object Activity : Screen("activity")
    data object Address : Screen("address")
    data object Profile : Screen("profile")
}

@Composable
fun VitoNavGraph(
    startDestination: String = Screen.Splash.route
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}