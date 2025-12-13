package com.example.shoppingapp.ui.view.fragment.edit_profile

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import com.example.shoppingapp.databinding.FragmentChangeEmailAndPhoneBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.auth.LoginActivity
import com.example.shoppingapp.ui.viewmodel.edit_profile.ChangeEmailAndPhoneViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeEmailAndPhoneFragment : Fragment() {

    private val viewModel: ChangeEmailAndPhoneViewModel by viewModels()
    private var _binding: FragmentChangeEmailAndPhoneBinding? = null
    private val binding get() = _binding!!
    private var isOnlyPhoneChange = true

    private val controller by lazy { findNavController() }

    private val email by lazy {
        arguments?.getString(StringConstants.EMAIL_BUNDLE)
    }
    private val phone by lazy {
        arguments?.getString(StringConstants.PHONE_BUNDLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeEmailAndPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.changePhoneState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is StateListener.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                            ToastUtils.showToast(requireContext(), it.e?.message.toString())
                        }

                        is StateListener.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (!viewModel.checkIsGoogleAccount()) {
                                binding.edtEmail.setText("")
                                binding.edtPhone.setText("")
                            } else {
                                binding.edtPhone.setText("")
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.changeEmailState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is StateListener.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                            ToastUtils.showToast(requireContext(), it.e?.message.toString())
                        }

                        is StateListener.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (!viewModel.checkIsGoogleAccount()) {
                                binding.edtEmail.setText("")
                                binding.edtPhone.setText("")
                            } else {
                                binding.edtPhone.setText("")
                            }
                            if (!isOnlyPhoneChange) {
                                val builder = AlertDialog.Builder(requireContext())
                                    .setTitle("Update Info")
                                    .setMessage("Please check your new email to verify\nAnd log in app again")
                                    .setIcon(R.drawable.icon_email)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        "Ok"
                                    ) { dialogInterface, _ ->
                                        viewModel.writeStateEmailVerify()
                                        dialogInterface.dismiss()
                                        requireActivity().startActivity(
                                            Intent(
                                                requireActivity(),
                                                LoginActivity::class.java
                                            )
                                        )
                                        requireActivity().finishAffinity()
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
            }
        }

        binding.apply {

            edtEmail.setText(email)
            edtPhone.setText(phone)

            if (viewModel.checkIsGoogleAccount()) {
                edtEmail.isEnabled = false
                txtLayoutEmail.helperText = "This is google account, you can not change this email"
                txtLayoutEmail.setHelperTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.button_color
                        )
                    )
                )
            }

            btnSave.setOnClickListener {
                val emailChange = edtEmail.text.toString().trim()
                val phoneChange = edtPhone.text.toString().trim()
                if (phoneChange.isNotEmpty() && phoneChange.length < 10) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Phone number must be equal to 10 digits"
                    )
                    return@setOnClickListener
                }
                if (emailChange == email && phoneChange == phone) {
                    ToastUtils.showToast(requireContext(), "hihi")
                    return@setOnClickListener
                } else if (emailChange == email && phoneChange != phone) {
                    val userDTOUpdate = UserDTOUpdate(
                        viewModel.getCurrentUserId(),
                        null,
                        null,
                        phoneChange,
                        null,
                        null
                    )
                    viewModel.changePhone(userDTOUpdate)
                    isOnlyPhoneChange = true
                } else if (emailChange != email && phoneChange == phone) {
                    val userDTOUpdate = UserDTOUpdate(
                        viewModel.getCurrentUserId(),
                        null,
                        emailChange,
                        null,
                        null,
                        null
                    )
                    viewModel.changeEmail(userDTOUpdate)
                    isOnlyPhoneChange = false
                } else {
                    val userDTOUpdate = UserDTOUpdate(
                        viewModel.getCurrentUserId(),
                        null,
                        emailChange,
                        phoneChange,
                        null,
                        null
                    )
                    viewModel.changeEmail(userDTOUpdate)
                    isOnlyPhoneChange = false
                }
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}