package com.example.shoppingapp.constants

import android.os.Bundle
import androidx.navigation.NavController
import com.example.shoppingapp.R

object CheckNavigation {

    fun navigateTo(navController: NavController, idActionHome: Int, idActionStore: Int){
        if(navController.graph.id == R.id.nav_home){
            navController.navigate(idActionHome)
        }
        else{
            navController.navigate(idActionStore)
        }
    }

    fun navigateTo(navController: NavController, idActionHome: Int, idActionStore: Int, bundle: Bundle){
        if(navController.graph.id == R.id.nav_home){
            navController.navigate(idActionHome, bundle)
        }
        else{
            navController.navigate(idActionStore, bundle)
        }
    }

}