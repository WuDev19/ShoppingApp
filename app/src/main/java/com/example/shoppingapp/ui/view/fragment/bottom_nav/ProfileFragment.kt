package com.example.shoppingapp.ui.view.fragment.bottom_nav

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.ui.view.activity.common.MainActivity
import com.example.shoppingapp.ui.viewmodel.bottom_nav.ProfileViewModel

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity = requireActivity() as MainActivity
//        mainActivity.onBackPressedDispatcher.addCallback(this@ProfileFragment){
////            mainActivity.setState(0, R.id.home)
//            ToastUtils.showToast(requireContext(), "profile")
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}