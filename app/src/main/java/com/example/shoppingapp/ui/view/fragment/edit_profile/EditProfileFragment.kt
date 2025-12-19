package com.example.shoppingapp.ui.view.fragment.edit_profile

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.graphics.BitmapCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.CustomDialogImageAvatarCircleBinding
import com.example.shoppingapp.databinding.CustomDialogImageDetailBinding
import com.example.shoppingapp.databinding.FragmentEditProfileBinding
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.edit_profile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.BinaryFormat

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val viewModel: EditProfileViewModel by viewModels()
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private lateinit var userProfile: UserProfile

    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private lateinit var urlAvt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri ->
            uri?.let {
                viewModel.uploadAvatarToCloudinary(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfileUser()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileUserState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> {
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                        }

                        is StateListener.Success -> {
                            userProfile = it.result
                            urlAvt = userProfile.avatar.toString()
                            binding.apply {
                                Glide.with(this@EditProfileFragment)
                                    .load(userProfile.avatar)
                                    .placeholder(R.drawable.place_holder)
                                    .error(R.drawable.icon_avatar)
                                    .into(imgAvt)
                                tvName.text = userProfile.ten ?: StringConstants.UNKNOWN_NAME
                                tvGender.text =
                                    if (userProfile.gioitinh == "ND") StringConstants.GENDER_NOT_DEFINE
                                    else userProfile.gioitinh ?: StringConstants.GENDER_NOT_DEFINE
                                tvEmailSetting.text =
                                    userProfile.email ?: StringConstants.EMAIL_EXAMPLE
                                tvUserId.text = userProfile.maND
                                tvPhone.text = userProfile.sdt ?: ""
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.avatarState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.isVisible = true
                        is StateListener.Error -> {
                            binding.progressBar.isVisible = false
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                        }
                        is StateListener.Success -> {
                            urlAvt = it.result
                            binding.progressBar.isVisible = false
                            Glide.with(this@EditProfileFragment)
                                .load(it.result)
                                .placeholder(R.drawable.place_holder)
                                .error(R.drawable.icon_avatar)
                                .into(binding.imgAvt)
                        }
                    }
                }
            }
        }

        // TODO: thêm click để hiện ảnh avt
        binding.apply {

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

            linearUsernameGender.setOnClickListener {
                val bundle = bundleOf(
                    StringConstants.USERNAME to userProfile.ten,
                    StringConstants.GENDER to userProfile.gioitinh
                )
                controller.navigate(R.id.action_editProfileFragment_to_changeNameFragment, bundle)
            }

            linearEmailPhone.setOnClickListener {
                val bundle = bundleOf(
                    StringConstants.EMAIL_BUNDLE to userProfile.email,
                    StringConstants.PHONE_BUNDLE to userProfile.sdt
                )
                controller.navigate(R.id.action_editProfileFragment_to_changeEmailAndPhoneFragment, bundle)
            }

            imgCopy.setOnClickListener {
                //clip manager cho phep sao chep hinh anh, text, du lieu giua cac app
                val clipBoard =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager //lay clipboard manager
                //clip data la doi tuong de chua du lieu muon copy vao clip board
                val clipData = ClipData.newPlainText(StringConstants.LABEL_CLIP_DATA, binding.tvUserId.text.toString())
                //thuc hien sao chep vao clip board
                clipBoard.setPrimaryClip(clipData)
                ToastUtils.showToast(requireContext(), StringConstants.COPY_ID)
            }

            imgChangeAvt.setOnClickListener {
                resultLauncher.launch("image/*")
            }

            imgAvt.setOnClickListener {
                val dialogView = CustomDialogImageAvatarCircleBinding.inflate(layoutInflater)
                Glide.with(requireContext())
                    .load(urlAvt)
                    .placeholder(R.drawable.place_holder)
                    .fitCenter()
                    .error(R.drawable.icon_avatar)
                    .into(dialogView.imgAnh)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView.root)
                    .create()
                dialog.show()
                dialog.window?.apply {
                    setGravity(Gravity.CENTER)
                    setLayout(
                        resources.displayMetrics.widthPixels * 80 / 100,
                        resources.displayMetrics.heightPixels * 50 / 100
                    )
                    setBackgroundDrawableResource(R.drawable.custom_dialog)
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}