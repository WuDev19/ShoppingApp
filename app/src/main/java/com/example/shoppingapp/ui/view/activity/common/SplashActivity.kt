package com.example.shoppingapp.ui.view.activity.common

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.databinding.ActivitySplashBinding
import com.example.shoppingapp.ui.view.activity.auth.LoginActivity
import com.example.shoppingapp.ui.viewmodel.common.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        lifecycleScope.launch {
            viewBinding.imgLogo
                .animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(1000)
                .start()
            viewModel.checkLogin()
            delay(2000)
            val user = viewModel.uiState.value
            if(user.id == ""){
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finishAffinity()
            }
            else{
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finishAffinity()
            }
        }

    }

}