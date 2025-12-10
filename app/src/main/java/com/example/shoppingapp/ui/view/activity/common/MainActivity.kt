package com.example.shoppingapp.ui.view.activity.common

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.ViewPagerUtils
import com.example.shoppingapp.databinding.ActivityMainBinding
import com.example.shoppingapp.ui.view.adapter.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityMainBinding
    private val viewPagerAdapter: ViewPagerAdapter by lazy { ViewPagerAdapter(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        viewbinding.viewPager2.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            viewbinding.bottomNavigationView.selectedItemId = R.id.home
                        }

                        1 -> {
                            viewbinding.bottomNavigationView.selectedItemId = R.id.store
                        }

                        else -> {
                            viewbinding.bottomNavigationView.selectedItemId = R.id.profile
                        }
                    }
                }
            })
        }

        viewbinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    viewbinding.viewPager2.currentItem = 0
                }

                R.id.store -> {
                    viewbinding.viewPager2.currentItem = 1
                }

                R.id.profile -> {
                    viewbinding.viewPager2.currentItem = 2
                }
            }
            true
        }

        onBackPressedDispatcher.addCallback {
            val pos = viewbinding.viewPager2.currentItem
            when (pos) {
                0 -> {
                    val navHomeHost =
                        supportFragmentManager.findFragmentByTag("f0") as NavHostFragment
                    val controller = navHomeHost.navController
                    if (controller.currentDestination?.id != R.id.homeFragment) {
                        controller.popBackStack()
                        ViewPagerUtils.enableViewPager(viewbinding.viewPager2, controller)
                        BottomNavigationUtils.showBottomNavigation(viewbinding.bottomNavigationView, controller)
                    } else {
                        finishAffinity()
                    }
                }

                1 -> {
                    val navStoreHost =
                        supportFragmentManager.findFragmentByTag("f1") as NavHostFragment
                    val controller = navStoreHost.navController
                    if (controller.currentDestination?.id != R.id.storeFragment) {
                        controller.popBackStack()
                        ViewPagerUtils.enableViewPager(viewbinding.viewPager2, controller)
                        BottomNavigationUtils.showBottomNavigation(viewbinding.bottomNavigationView, controller)
                    } else {
                        setState()
                    }
                }

                2 -> {
                    val navProfileHost =
                        supportFragmentManager.findFragmentByTag("f2") as NavHostFragment
                    val controller = navProfileHost.navController
                    if (controller.currentDestination?.id != R.id.profileFragment) {
                        controller.popBackStack()
                        BottomNavigationUtils.showBottomNavigation(viewbinding.bottomNavigationView, controller)
                    } else {
                        setState()
                    }
                }

            }
        }
    }

    private fun setState() {
        viewbinding.viewPager2.currentItem = 0
        viewbinding.bottomNavigationView.selectedItemId = R.id.home
    }

}