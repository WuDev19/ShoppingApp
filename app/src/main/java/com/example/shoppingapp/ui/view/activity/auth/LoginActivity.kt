package com.example.shoppingapp.ui.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.ActivityLoginBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.common.MainActivity
import com.example.shoppingapp.ui.viewmodel.auth.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewBinding : ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        lifecycleScope.launch {
            viewModel.state.collectLatest {
                when(it){
                    is StateListener.Loading -> viewBinding.progressBar.visibility = View.VISIBLE
                    is StateListener.Success -> {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finishAffinity()
                    }
                    is StateListener.Error -> {
                        ToastUtils.showToast(this@LoginActivity, "Some things went wrong")
                        viewBinding.progressBar.visibility = View.GONE
                    }
                    is StateListener.Idle -> viewBinding.progressBar.visibility = View.GONE
                }
            }
        }

        viewBinding.btnSignIn.setOnClickListener{
            val email = viewBinding.edtEmail.text.toString()
            val password = viewBinding.edtPassword.text.toString()
            viewModel.loginWithEmail(email, password)
        }

        viewBinding.btnCreateAccount.setOnClickListener{
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }

    }
}