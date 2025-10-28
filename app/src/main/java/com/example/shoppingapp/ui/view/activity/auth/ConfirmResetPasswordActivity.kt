package com.example.shoppingapp.ui.view.activity.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.ActivityConfirmResetPasswordBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.auth.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConfirmResetPasswordActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityConfirmResetPasswordBinding
    private val viewmodel: ResetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewbinding = ActivityConfirmResetPasswordBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
        val email = intent.getStringExtra(StringConstants.EMAIL_KEY_INTENT)
        viewbinding.tvEmail.text = email ?: ""

        lifecycleScope.launch {
            viewmodel.state.collectLatest {
                when(it){
                    is StateListener.Idle -> {}
                    is StateListener.Error -> {
                        ToastUtils.showToast(this@ConfirmResetPasswordActivity, StringConstants.HAS_ERROR)
                    }
                    is StateListener.Loading -> {}
                    is StateListener.Success -> {
                        ToastUtils.showToast(this@ConfirmResetPasswordActivity, StringConstants.CHECK_EMAIL)
                    }
                }
            }
        }

        viewbinding.btnDone.setOnClickListener {
            val intent = Intent(this@ConfirmResetPasswordActivity, LoginActivity::class.java)
            intent.putExtra(StringConstants.EMAIL_KEY_INTENT, email)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        viewbinding.imgBack.setOnClickListener {
            finish()
        }

        viewbinding.tvResendEmail.setOnClickListener {
            if (email != null) {
                viewmodel.resetPassword(email)
            }
            else {
                ToastUtils.showToast(this@ConfirmResetPasswordActivity, StringConstants.HAS_ERROR)
            }
        }

    }
}