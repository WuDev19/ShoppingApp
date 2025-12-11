package com.example.shoppingapp.ui.view.fragment.edit_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import com.example.shoppingapp.databinding.FragmentChangeNameGenderBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.edit_profile.ChangeNameAndGenderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeNameAndGenderFragment : Fragment() {

    private val viewModel: ChangeNameAndGenderViewModel by viewModels()
    private var _binding: FragmentChangeNameGenderBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private var lastCheck = -1

    private var gender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeNameGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.changeNameState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is StateListener.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                        }

                        is StateListener.Success -> {
                            binding.progressBar.visibility = View.GONE
                            ToastUtils.showToast(requireContext(), "Update Successfully")
                            binding.apply {
                                edtFirstName.setText("")
                                edtLastName.setText("")
                                radioGroup.clearCheck()
                            }
                        }
                    }
                }
            }
        }

        binding.apply {
            btnSave.setOnClickListener {
                val firstName = edtFirstName.text.toString()
                val lastName = edtLastName.text.toString()
                val fullName = buildString {
                    append(firstName)
                    append(" ")
                    append(lastName)
                }
                viewModel.updateName(
                    UserDTOUpdate(
                        viewModel.getCurrentUserId(),
                        fullName,
                        null,
                        null,
                        null,
                        gender
                    )
                )
            }

            radioButtonMale.setOnClickListener { view ->
                if (view.id == lastCheck) {
                    radioGroup.clearCheck()
                    lastCheck = -1
                    gender = null
                } else {
                    lastCheck = view.id
                    gender = radioButtonMale.text.toString()
                }
            }

            radioButtonFemale.setOnClickListener { view ->
                if (view.id == lastCheck) {
                    radioGroup.clearCheck()
                    lastCheck = -1
                    gender = null
                } else {
                    lastCheck = view.id
                    gender = radioButtonFemale.text.toString()
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