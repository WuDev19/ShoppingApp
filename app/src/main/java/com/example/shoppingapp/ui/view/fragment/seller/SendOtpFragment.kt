package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentSendOtpBinding
import com.example.shoppingapp.ui.viewmodel.seller.SendOtpViewModel

class SendOtpFragment : Fragment() {

    private val viewModel: SendOtpViewModel by viewModels()
    private var _binding: FragmentSendOtpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            btnSendOtp.setOnClickListener {
                val verifyOtpFragment = VerifyOtpFragment{

                }
                verifyOtpFragment.show(requireActivity().supportFragmentManager, verifyOtpFragment.tag)
                verifyOtpFragment.isCancelable = false
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}