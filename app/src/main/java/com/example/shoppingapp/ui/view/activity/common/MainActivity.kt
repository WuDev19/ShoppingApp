package com.example.shoppingapp.ui.view.activity.common

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.constants.ViewPagerUtils
import com.example.shoppingapp.databinding.ActivityMainBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.auth.LoginActivity
import com.example.shoppingapp.ui.view.adapter.ViewPagerAdapter
import com.example.shoppingapp.ui.viewmodel.common.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewbinding: ActivityMainBinding
    private val viewPagerAdapter: ViewPagerAdapter by lazy { ViewPagerAdapter(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //chỗ này là khi gửi link email mới và đăng nhập để vào main activity, vào main thì cập nhật lại db, nếu lỗi thì cho logout và login lại
                viewModel.updateEmailState.collectLatest {
                    if (it is StateListener.Error) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                            .setTitle("Ouchh")
                            .setMessage(StringConstants.HAS_ERROR + " \nPlease log out and log in again!")
                            .setIcon(R.drawable.icon_warning)
                            .setCancelable(false)
                            .setPositiveButton(
                                "Ok"
                            ) { _, _ ->
                                viewModel.logout()
                            }
                        val dialog = builder.create()
                        dialog.show()
                        dialog.window?.apply {
                            setBackgroundDrawableResource(R.drawable.custom_dialog)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutState.collectLatest {
                    if (it is StateListener.Success) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                }
            }
        }

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
                        BottomNavigationUtils.showBottomNavigation(
                            viewbinding.bottomNavigationView,
                            controller
                        )
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
                        BottomNavigationUtils.showBottomNavigation(
                            viewbinding.bottomNavigationView,
                            controller
                        )
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
                        ViewPagerUtils.enableViewPager(viewbinding.viewPager2, controller)
                        BottomNavigationUtils.showBottomNavigation(
                            viewbinding.bottomNavigationView,
                            controller
                        )
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

    override fun onStart() {
        super.onStart()
        if (!viewModel.getStateEmailVerify()) {
            lifecycleScope.launch {
                try {
                    val currentUser = viewModel.auth.currentUser
                    currentUser?.reload()?.await()
                    //neu da xac nhan roi thi cap nhap email luon
                    if (currentUser?.isEmailVerified == true) {
                        viewModel.confirmUpdateEmail()
                        viewModel.writeStateEmailVerify()
                    }
                } catch (e: Exception) {
                    Log.e(StringConstants.DEBUG, "onStart: ${e.message.toString()}")
                }
            }
        }
    }

}