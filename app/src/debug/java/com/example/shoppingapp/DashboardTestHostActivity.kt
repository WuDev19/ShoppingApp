package com.example.shoppingapp

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint

/** Local device-test host. Excluded from release and not exported. */
@AndroidEntryPoint
class DashboardTestHostActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val host = NavHostFragment()
            supportFragmentManager.beginTransaction().replace(android.R.id.content, host).commitNow()
            val graph = host.navController.navInflater.inflate(R.navigation.nav_profile)
            graph.setStartDestination(R.id.dashBoardSellerFragment)
            host.navController.setGraph(graph, Bundle().apply { putString("userId", "dashboard-device-isolated") })
        }
    }
}
