package com.example.shoppingapp.ui.view.fragment.user

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentWriteReviewBinding
import com.example.shoppingapp.domain.model.SelectedStar
import com.example.shoppingapp.ui.view.adapter.StarAdapter
import com.example.shoppingapp.ui.viewmodel.user.WriteReviewViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteReviewFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentWriteReviewBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: WriteReviewViewModel by viewModels()
    private val listStar by lazy {
        listOf(
            SelectedStar(false),
            SelectedStar(false),
            SelectedStar(false),
            SelectedStar(false),
            SelectedStar(false)
        )
    }
    private val starAdapter by lazy {
        StarAdapter(listStar = listStar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recyclerViewStar.apply {
                adapter = starAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}