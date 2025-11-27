package com.example.shoppingapp.constants

import androidx.navigation.NavController
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R

object ViewPagerUtils {

    fun disableViewPager(viewPager2: ViewPager2) {
        if (viewPager2.isUserInputEnabled) {
            viewPager2.isUserInputEnabled = false
        }
    }

    fun enableViewPager(viewPager2: ViewPager2, navController: NavController) {
        if (!viewPager2.isUserInputEnabled && (
                    navController.currentDestination?.id == R.id.homeFragment ||
                            navController.currentDestination?.id == R.id.storeFragment)
        ) {
            viewPager2.isUserInputEnabled = true
        }
    }

}