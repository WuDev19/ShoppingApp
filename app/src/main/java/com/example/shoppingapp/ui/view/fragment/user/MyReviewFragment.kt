package com.example.shoppingapp.ui.view.fragment.user

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentMyReviewBinding
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.SelectedStar
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ImageShowAdapter
import com.example.shoppingapp.ui.view.adapter.StarAdapter
import com.example.shoppingapp.ui.viewmodel.user.MyReviewViewModel
import com.example.shoppingapp.utils.StringConstants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyReviewFragment : BottomSheetDialogFragment() {
    private val TAG = MyReviewFragment::class.simpleName
    private var _binding: FragmentMyReviewBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: MyReviewViewModel by viewModels()
    private val reviewId by lazy {
        arguments?.getInt(StringConstants.REVIEW_ID)
    }
    private val listStar by lazy {
        listOf(
            SelectedStar(false),
            SelectedStar(false),
            SelectedStar(false),
            SelectedStar(false),
            SelectedStar(false)
        )
    }
    private var star: Int? = null
    private val starAdapter by lazy {
        StarAdapter(listStar = listStar) { star ->
            this.star = star
        }
    }
    private val imageShowAdapter by lazy {
        ImageShowAdapter(emptyList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewId?.let { viewModel.getProductReviewDetail(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productReviewDetailState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                Log.e(TAG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success<ProductReview> -> {
                                Log.e(TAG, "onViewCreated: ${it.result}")
                            }
                        }
                    }
                }
            }
        }
        binding.apply {
            recyclerViewStar.apply {
                adapter = starAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            recyclerViewReviewImage.apply {
                adapter = imageShowAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}