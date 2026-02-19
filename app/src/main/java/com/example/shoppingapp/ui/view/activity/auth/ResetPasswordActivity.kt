package com.example.shoppingapp.ui.view.activity.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.databinding.ActivityResetPasswordBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.auth.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityResetPasswordBinding
    private val viewmodel: ResetPasswordViewModel by viewModels()
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewbinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        lifecycleScope.launch {
            viewmodel.state.collectLatest {
                when(it){
                    is StateListener.Idle -> {}
                    is StateListener.Error -> {
                        ToastUtils.showToast(this@ResetPasswordActivity, StringConstants.HAS_ERROR)
                    }
                    is StateListener.Loading -> {}
                    is StateListener.Success -> {
                        ToastUtils.showToast(this@ResetPasswordActivity, StringConstants.CHECK_EMAIL)
                        val intent = Intent(this@ResetPasswordActivity, ConfirmResetPasswordActivity::class.java)
                        intent.putExtra(StringConstants.EMAIL_KEY_INTENT, email)
                        startActivity(intent)
                    }
                }
            }
        }

        viewbinding.imgBack.setOnClickListener {
            finish()
        }

        viewbinding.btnSubmit.setOnClickListener{
            email = viewbinding.edtEmail.text.toString()
            viewmodel.resetPassword(email)
        }

    }

}