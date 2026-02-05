package com.example.shoppingapp.ui.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.ActivityCreateAccountBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.common.MainActivity
import com.example.shoppingapp.ui.viewmodel.auth.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountActivity : AppCompatActivity() {
    private val viewModel: CreateAccountViewModel by viewModels()
    private lateinit var viewBinding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        lifecycleScope.launch {
            viewModel.state.collectLatest {
                when(it){
                    is StateListener.Idle -> viewBinding.progressBarCreate.visibility = View.GONE
                    is StateListener.Loading -> viewBinding.progressBarCreate.visibility = View.VISIBLE
                    is StateListener.Error -> {
                        ToastUtils.showToast(this@CreateAccountActivity, it.e.toString())
                        Log.e("CheckUser", "onCreate: ${it.e.toString()}")
                        viewBinding.progressBarCreate.visibility = View.GONE
                    }
                    is StateListener.Success -> {
                        val dataLogin = it.result
                        Log.e(StringConstants.SEND_OTP_SUCCESS, "onCreate: ${dataLogin.email} và ${dataLogin.password}")
                        val intentToLogin = Intent(this@CreateAccountActivity, LoginActivity::class.java)
                        intentToLogin.putExtra(StringConstants.EMAIL_KEY_INTENT, dataLogin.email)
                        intentToLogin.putExtra(StringConstants.PASSWORD, dataLogin.password)
                        startActivity(intentToLogin)
                        finishAffinity()
                    }
                }
            }
        }

        viewBinding.btnCreate.setOnClickListener{
            val firstName = viewBinding.edtFirstName.text.toString()
            val lastName = viewBinding.edtLastName.text.toString()
            val email = viewBinding.edtEmailCreate.text.toString()
            val phone = viewBinding.edtPhone.text.toString()
            val password = viewBinding.edtPasswordCreate.text.toString()
            if(firstName.isEmpty() && lastName.isEmpty()){
                ToastUtils.showToast(this@CreateAccountActivity, "Please enter username")
                return@setOnClickListener
            }
            if(email.isEmpty()){
                ToastUtils.showToast(this@CreateAccountActivity, "Email is required")
                return@setOnClickListener
            }
            if (password.isEmpty()){
                ToastUtils.showToast(this@CreateAccountActivity, "Please enter password")
                return@setOnClickListener
            }
            viewModel.createAccount(email, password, phone, "$firstName $lastName")
        }

        viewBinding.imgBack.setOnClickListener {
            finish()
        }

    }
}