package com.example.shoppingapp.ui.view.activity.auth

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.databinding.ActivityLoginBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.common.MainActivity
import com.example.shoppingapp.ui.viewmodel.auth.LoginViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var email: String
    private lateinit var password: String

    @Inject
    lateinit var credentialManager: CredentialManager

    @Inject
    lateinit var request: GetCredentialRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.apply {

            intent?.apply {
                email = getStringExtra(StringConstants.EMAIL_KEY_INTENT) ?: ""
                password = getStringExtra(StringConstants.PASSWORD) ?: ""
                edtEmail.setText(email)
                edtPassword.setText(password)
                Log.e(StringConstants.SEND_OTP_SUCCESS, "onCreate: $email và $password")
            }

            btnSignIn.setOnClickListener {
                val mEmail = viewBinding.edtEmail.text.toString().trim()
                val password = viewBinding.edtPassword.text.toString().trim()
                viewModel.loginWithEmail(mEmail, password)
            }

            btnCreateAccount.setOnClickListener {
                startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
            }

            tvForgotPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
            }

            imgGoogle.setOnClickListener {
                lifecycleScope.launch {
                    try {
                        val credentialResponse =
                            credentialManager.getCredential(this@LoginActivity, request)
                        val credential = credentialResponse.credential
                        handleSignIn(credential)
                    } catch (e: Exception) {
                        if (e is NoCredentialException) {
                            ToastUtils.showToast(
                                this@LoginActivity,
                                StringConstants.NO_ACCOUNT_EXCEPTION
                            )
                        }
                        Log.e("e", "onCreate: $e")
                    }
                }
            }

        }

        lifecycleScope.launch {
            viewModel.state.collectLatest {
                when (it) {
                    is StateListener.Idle -> viewBinding.progressBar.visibility = View.GONE
                    is StateListener.Loading -> viewBinding.progressBar.visibility = View.VISIBLE
                    is StateListener.Error -> {
                        ToastUtils.showToast(this@LoginActivity, StringConstants.HAS_ERROR)
                        Log.e("checkkk", "onCreate: ${it.e.toString()}")
                        viewBinding.progressBar.visibility = View.GONE
                    }

                    is StateListener.Success -> {
                        Log.e(StringConstants.DEBUG, "onCreate: ${it.result}")
                        viewModel.saveTokenToLocal(it.result)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finishAffinity()
                    }
                }
            }
        }

    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential &&
            credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            viewModel.loginWithGoogle(idToken)
        } else {
            ToastUtils.showToast(this@LoginActivity, StringConstants.HAS_ERROR)
        }
    }

}