package com.example.shoppingapp.ui.view.fragment.seller

import android.graphics.Typeface
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.FragmentVerifyOtpBinding
import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.seller.VerifyOtpViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class VerifyOtpFragment(
    private val onLoading: () -> Unit,
    private val onError: (message: String) -> Unit,
    private val onSuccess: () -> Unit
) : BottomSheetDialogFragment() {

    private val viewModel: VerifyOtpViewModel by viewModels()
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!
    private val uriAnh: Uri? by lazy {
        arguments?.let {
            BundleCompat.getParcelable(it, StringConstants.URI_ANH, Uri::class.java)
        }
    }
    private val shopName by lazy {
        arguments?.getString(StringConstants.SHOP_NAME)
    }
    private val shopAddress by lazy {
        arguments?.getString(StringConstants.SHOP_ADDRESS)
    }
    private val otpModel by lazy {
        arguments?.let {
            BundleCompat.getParcelable(it, StringConstants.OTP_MODEL, OtpModel::class.java)
        }
    }
    private var isExpire: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                tvResendOtp.isEnabled = false
                tvResendOtp.setTypeface(null, Typeface.NORMAL)
                otpModel?.expireTime?.let {
                    countDown(it)
                        .flowOn(Dispatchers.Default)
                        .onCompletion {
                            tvResendOtp.isEnabled = true
                            tvResendOtp.setTypeface(null, Typeface.BOLD)
                        }
                        .collect { i ->
                            if (i == 0) {
                                isExpire = true
                            }
                            tvExpire.text = buildString {
                                append("Expire in ")
                                append(i)
                                append("s")
                            }
                        }
                }
            }

            handleEnterOTP()

            btnVerify.setOnClickListener {
                if (!isExpire) {
                    ToastUtils.showToast(requireContext(), "Van con han")
                } else {
                    ToastUtils.showToast(requireContext(), "Het han")
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun countDown(expire: Int) = flow {
        for (i in expire downTo 0) {
            emit(i)
            delay(1000)
        }
    }

    private fun handleEnterOTP(){
        binding.apply {
            edt1.requestFocus()
            edt1.addTextChangedListener { editable ->
                if (editable.toString().length == 1) {
                    edt2.isEnabled = true
                    edt2.requestFocus()
                }
            }
            edt2.addTextChangedListener { editable ->
                if (editable.toString().length == 1) {
                    edt3.isEnabled = true
                    edt3.requestFocus()
                } else if (editable.toString().isEmpty()) {
                    edt1.requestFocus()
                }
            }
            edt3.addTextChangedListener { editable ->
                if (editable.toString().length == 1) {
                    edt4.isEnabled = true
                    edt4.requestFocus()
                } else if (editable.toString().isEmpty()) {
                    edt2.requestFocus()
                }
            }
            edt4.addTextChangedListener { editable ->
                if (editable.toString().length == 1) {
                    edt5.isEnabled = true
                    edt5.requestFocus()
                } else if (editable.toString().isEmpty()) {
                    edt3.requestFocus()
                }
            }
            edt5.addTextChangedListener { editable ->
                if (editable.toString().length == 1) {
                    edt6.isEnabled = true
                    edt6.requestFocus()
                } else if (editable.toString().isEmpty()) {
                    edt4.requestFocus()
                }
            }
            edt6.addTextChangedListener { editable ->
                if (editable.toString().length == 1) {
                    isCancelable = true
                } else if (editable.toString().isEmpty()) {
                    edt5.requestFocus()
                }
            }
        }
    }

}