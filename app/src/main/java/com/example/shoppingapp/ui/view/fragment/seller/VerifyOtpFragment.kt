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
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.request.SellerRequest
import com.example.shoppingapp.databinding.FragmentVerifyOtpBinding
import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.seller.SendOtpViewModel
import com.example.shoppingapp.ui.viewmodel.seller.VerifyOtpViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VerifyOtpFragment(private val onSuccess: () -> Unit) : BottomSheetDialogFragment() {

    private val viewModel: VerifyOtpViewModel by viewModels()
    private val parentViewModel: SendOtpViewModel by viewModels(ownerProducer = {
        requireParentFragment()
    })
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
    private val shopPhone by lazy {
        arguments?.getString(StringConstants.SHOP_PHONE)
    }
    private val otpModel by lazy {
        arguments?.let {
            BundleCompat.getParcelable(it, StringConstants.OTP_MODEL, OtpModel::class.java)
        }
    }
    private val provinceId by lazy {
        arguments?.getInt(StringConstants.PROVINCE_ID)
    }
    private val districtId by lazy {
        arguments?.getInt(StringConstants.DISTRICT_ID)
    }
    private val wardCode by lazy {
        arguments?.getString(StringConstants.WARD_CODE)
    }
    private var isExpire: Boolean = false

    @Inject
    lateinit var auth: FirebaseAuth

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

            handleEnterOTP()

            btnVerify.setOnClickListener {
                if (!isExpire) {
                    if (uriAnh == null ||
                        shopName == null ||
                        shopPhone == null ||
                        shopAddress == null ||
                        provinceId == null ||
                        districtId == null ||
                        wardCode == null
                    ) {
                        ToastUtils.showToast(requireContext(), StringConstants.HAS_ERROR)
                        return@setOnClickListener
                    }
                    binding.shimmer.isVisible = true
                    binding.progressBar.isVisible = true
                    binding.constraintMain.isEnabled = false
                    this@VerifyOtpFragment.isCancelable = false
                    val otp = buildString {
                        append(edt1.text.toString())
                        append(edt2.text.toString())
                        append(edt3.text.toString())
                        append(edt4.text.toString())
                        append(edt5.text.toString())
                        append(edt6.text.toString())
                    }
                    viewModel.register(
                        otp,
                        uriAnh!!,
                        SellerRequest(
                            auth.currentUser?.uid!!,
                            shopName!!,
                            shopPhone!!,
                            shopAddress!!,
                            provinceId!!,
                            districtId!!,
                            wardCode!!,
                            "",
                            "ADMIN"
                        )
                    )
                } else {
                    ToastUtils.showToast(requireContext(), StringConstants.CODE_EXPIRE)
                }
            }

            tvResendOtp.setOnClickListener {
                parentViewModel.sendOtp()
            }

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

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.registerState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                binding.constraintMain.isEnabled = true
                                binding.shimmer.isVisible = false
                                binding.progressBar.isVisible = false
                                this@VerifyOtpFragment.isCancelable = true
                                Log.e(
                                    StringConstants.DEBUG,
                                    "onViewCreated: ${it.e?.message.toString()}"
                                )
                                ToastUtils.showToast(requireContext(), it.e?.message.toString())
                            }

                            is StateListener.Success<AuthResponse> -> {
                                val data = it.result
                                Log.e(StringConstants.DEBUG, "onViewCreated: $data")
                                viewModel.saveTokenToLocal(data)
                                ToastUtils.showToast(
                                    requireContext(),
                                    StringConstants.REGISTER_SELLER_SUCCESS
                                )
                                binding.constraintMain.isEnabled = true
                                binding.shimmer.isVisible = false
                                binding.progressBar.isVisible = false
                                this@VerifyOtpFragment.isCancelable = true
                                onSuccess()
                                this@VerifyOtpFragment.dismiss()
                            }
                        }
                    }
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

    private fun handleEnterOTP() {
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