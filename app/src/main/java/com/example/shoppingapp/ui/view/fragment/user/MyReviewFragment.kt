package com.example.shoppingapp.ui.view.fragment.user

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentMyReviewBinding
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.ReviewImage
import com.example.shoppingapp.domain.model.SelectedStar
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ImageDescriptionAdapter
import com.example.shoppingapp.ui.view.adapter.ImageShowAdapter
import com.example.shoppingapp.ui.view.adapter.StarAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.user.MyReviewViewModel
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.utils.UriHelper
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
    private val controller by lazy {
        requireParentFragment().findNavController()
    }
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
    private lateinit var imageShowAdapter: ImageShowAdapter
    private lateinit var imageDescriptionAdapter: ImageDescriptionAdapter
    private val listUri: MutableList<Uri> by lazy { mutableListOf() }
    private val listReviewImages: MutableList<ReviewImage> by lazy { mutableListOf() }
    private val listDeleteReviewImages: MutableList<Int> by lazy { mutableListOf() }
    private var uri: Uri? = null
    private val resultGallery: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
            binding.tvNtts2.isVisible = false
            listUri.addAll(result)
            imageDescriptionAdapter.submitList(listUri.toList())
        }
    private val takePictureResult: ActivityResultLauncher<Uri?> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                binding.tvNtts2.isVisible = false
                listUri.add(uri!!)
                imageDescriptionAdapter.submitList(listUri.toList())
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewId?.let { viewModel.getProductReviewDetail(it) }
        imageShowAdapter = ImageShowAdapter(
            emptyList(),
            callback = {
                val bundle = bundleOf(
                    StringConstants.IMAGE_DESCRIPTION_SLIDER to listReviewImages.map { it.imageUrl }
                )
                controller.navigate(
                    R.id.action_myOrderFragment_to_imageDescriptionProductFragment3,
                    bundle
                )
            },
            delete = { reviewImage ->
                listReviewImages.remove(reviewImage)
                listDeleteReviewImages.add(reviewImage.reviewImageId)
                imageShowAdapter.updateImages(listReviewImages)
                binding.tvNtts1.isVisible = listReviewImages.isEmpty()
            })
        imageDescriptionAdapter = ImageDescriptionAdapter(
            callback = {
                val bundle = bundleOf(
                    StringConstants.IMAGE_DESCRIPTION_SLIDER to listUri.map { it.toString() }
                )
                controller.navigate(
                    R.id.action_myOrderFragment_to_imageDescriptionProductFragment3,
                    bundle
                )
            },
            delete = { uri ->
                listUri.remove(uri)
                imageDescriptionAdapter.submitList(listUri.toList())
                binding.tvNtts2.isVisible = listUri.isEmpty()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    // TODO: xem lại cái reviewId
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
                                val productReview = it.result
                                listReviewImages.clear()
                                listReviewImages.addAll(productReview.images)
                                binding.tvNtts1.isVisible = listReviewImages.isEmpty()
                                Log.e(TAG, "onViewCreated: ${it.result}")
                                this@MyReviewFragment.star = productReview.rating
                                binding.apply {
                                    edtReview.setText(productReview.comment)
                                    for (i in 0..<productReview.rating) {
                                        listStar[i].selected = true
                                    }
                                    starAdapter.updateStarFromServer(listStar)
                                    imageShowAdapter.updateImages(productReview.images)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateReviewState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
                                binding.layoutShimmer.frameShimmer.isVisible = true
                                this@MyReviewFragment.isCancelable = false
                            }

                            is StateListener.Error -> {
                                Log.e(TAG, "onViewCreated: ${it.e?.message}")
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                this@MyReviewFragment.isCancelable = true
                            }

                            is StateListener.Success<String> -> {
                                Log.e(TAG, "onViewCreated: ${it.result}")
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                this@MyReviewFragment.isCancelable = true
                                this@MyReviewFragment.dismiss()
                            }
                        }
                    }
                }
            }
        }
        binding.apply {
            binding.tvNtts2.isVisible = listUri.isEmpty()
            recyclerViewStar.apply {
                adapter = starAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            recyclerViewReviewImage.apply {
                adapter = imageShowAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
                addItemDecoration(MarginItemDecoration(5))
            }
            layoutAddImage.recyclerViewImage.apply {
                adapter = imageDescriptionAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
                addItemDecoration(MarginItemDecoration(5))
            }
            layoutAddImage.imgTakePhoto.setOnClickListener {
                uri = UriHelper.createImageUri(requireContext())
                if (uri == null) {
                    ToastUtils.showToast(requireContext(), StringConstants.HAS_ERROR)
                    return@setOnClickListener
                }
                takePictureResult.launch(uri)
            }
            layoutAddImage.imgGallery.setOnClickListener {
                resultGallery.launch(StringConstants.LAUNCH_IMAGE)
            }
            btnEditReview.setOnClickListener {
                val comment = edtReview.text.toString().trim()
                val star = star
                Log.e(TAG, "onViewCreated: $reviewId")
                viewModel.updateReview(reviewId, comment, star!!, listUri, listDeleteReviewImages)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}