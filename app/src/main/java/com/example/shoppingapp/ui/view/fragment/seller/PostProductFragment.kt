package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentPostProductBinding
import com.example.shoppingapp.ui.viewmodel.seller.PostProductViewModel

class PostProductFragment : Fragment() {

    private val viewModel: PostProductViewModel by viewModels()
    private var _binding: FragmentPostProductBinding? = null
    private val binding
        get() = _binding!!

    private val controller by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btAddProductDetail.setOnClickListener {
                controller.navigate(R.id.action_postProductFragment_to_postProductDetailFragment)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}