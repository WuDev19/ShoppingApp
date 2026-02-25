package com.example.shoppingapp.utils

import android.os.Bundle
import androidx.navigation.NavController
import com.example.shoppingapp.R

object CheckNavigation {

    fun navigateTo(
        navController: NavController,
        idActionHome: Int,
        idActionStore: Int,
        idActionProfile: Int? = null
    ) {
        when (navController.graph.id) {
            R.id.nav_home -> {
                navController.navigate(idActionHome)
            }
            R.id.nav_store -> {
                navController.navigate(idActionStore)
            }
            else -> {
                idActionProfile?.let { navController.navigate(it) }
            }
        }
    }

    fun navigateTo(
        navController: NavController,
        idActionHome: Int,
        idActionStore: Int,
        bundle: Bundle,
        idActionProfile: Int? = null
    ) {
        when (navController.graph.id) {
            R.id.nav_home -> {
                navController.navigate(idActionHome, bundle)
            }
            R.id.nav_store -> {
                navController.navigate(idActionStore, bundle)
            }
            else -> {
                idActionProfile?.let { navController.navigate(it, bundle) }
            }
        }
    }

}