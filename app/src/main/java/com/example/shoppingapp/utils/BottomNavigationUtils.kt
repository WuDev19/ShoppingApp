package com.example.shoppingapp.utils

import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.example.shoppingapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavigationUtils {

    fun hideBottomNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.animate()
            .setDuration(500)
            .alpha(0f)
            .withEndAction {
                bottomNavigationView.isVisible = false
            }
            .start()
    }

    fun showBottomNavigation(
        bottomNavigationView: BottomNavigationView,
        navController: NavController
    ) {
        if (!bottomNavigationView.isVisible && (
                    navController.currentDestination?.id == R.id.homeFragment ||
                            navController.currentDestination?.id == R.id.storeFragment ||
                            navController.currentDestination?.id == R.id.profileFragment)
        ) {
            bottomNavigationView.isVisible = true
            bottomNavigationView
                .animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }
    }

}