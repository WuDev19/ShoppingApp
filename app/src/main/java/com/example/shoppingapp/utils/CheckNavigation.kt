package com.example.shoppingapp.utils

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
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

    fun navigateToWhenPurchase(
        navController: NavController,
        idActionHome: Int,
        idActionStore: Int,
        bundle: Bundle,
    ) {
        when (navController.graph.id) {
            R.id.nav_home -> {
                navController.navigate(
                    idActionHome,
                    bundle,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false).build()
                )
            }

            R.id.nav_store -> {
                navController.navigate(
                    idActionStore, bundle, NavOptions.Builder()
                        .setPopUpTo(R.id.storeFragment, false).build()
                )
            }
        }
    }

}