package com.example.shoppingapp.ui.view.fragment.seller

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.databinding.FragmentSendOtpBinding
import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.seller.SendOtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendOtpFragment : Fragment() {
    private val viewmodel: SendOtpViewModel by viewModels()
    private var _binding: FragmentSendOtpBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private val argsRegister: SendOtpFragmentArgs by navArgs()

    private val uriAnh: Uri?
        get() = argsRegister.uriAnh

    private val shopName: String
        get() = argsRegister.shopName

    private val shopAddress: String
        get() = argsRegister.shopAddress

    private val resultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            ToastUtils.showToast(requireContext(), "Accepted")
            viewmodel.sendOtp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(StringConstants.DEBUG, "onCreate: $uriAnh")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

            btnSendOtp.setOnClickListener {
                val phone = edtPhone.text.toString().trim()
                if(phone.isEmpty() || phone.length < 9){
                    ToastUtils.showToast(requireContext(), "Please enter shop phone number")
                    return@setOnClickListener
                }
                //phải xin quyền runtime thì mới gửi notification được (android 13+)
                checkPermission()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewmodel.otpState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                ToastUtils.showToast(requireContext(), StringConstants.HAS_ERROR)
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success<OtpModel> -> {
                                val otpModel = it.result
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${otpModel.message}")
                                viewmodel.setSate()
                                createNotification(otpModel)
                                showVerifyFragment(otpModel)
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

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewmodel.sendOtp()
            }
        } else {
            viewmodel.sendOtp()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun createNotification(otpModel: OtpModel){
        val notification = NotificationCompat.Builder(
            requireContext(),
            StringConstants.SEND_OTP_CHANEL_ID
        )
            .setContentTitle("ShoppingApp")
            .setContentText(otpModel.message)
            .setSmallIcon(R.drawable.icon_avatar)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(otpModel.message)
                    .setSummaryText("OTP")
            )
            .build()
        val manager = NotificationManagerCompat.from(requireContext())
        manager.notify(1, notification)
    }

    private fun showVerifyFragment(otpModel: OtpModel) {
        binding.apply {
            val verifyOtpFragment = VerifyOtpFragment{
                controller.popBackStack()
            }
            Log.e(StringConstants.DEBUG, "kajshda: $shopName va \n $shopAddress")
            val bundle = bundleOf(
                StringConstants.URI_ANH to uriAnh,
                StringConstants.SHOP_NAME to shopName,
                StringConstants.SHOP_ADDRESS to shopAddress,
                StringConstants.OTP_MODEL to otpModel,
                StringConstants.SHOP_PHONE to edtPhone.text.toString().trim()
            )
            verifyOtpFragment.arguments = bundle
            val frag = childFragmentManager.findFragmentByTag("verifyFragment") as? VerifyOtpFragment
            frag?.dismiss()
            verifyOtpFragment.show(
                childFragmentManager,
                "verifyFragment"
            )
            verifyOtpFragment.isCancelable = false
        }
    }

}