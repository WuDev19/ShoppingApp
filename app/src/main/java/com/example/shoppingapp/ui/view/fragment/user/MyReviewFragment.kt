package com.example.shoppingapp.ui.view.fragment.user

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.example.shoppingapp.utils.ReviewImageUris
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyReviewFragment : BottomSheetDialogFragment() {
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
    private var draftComment: String? = null
    private var detailApplied = false
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
    private val resultGallery: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { result ->
            val remaining = (StringConstants.REVIEW_MAX_IMAGES - listUri.size - listReviewImages.size).coerceAtLeast(0)
            val selected = result.distinct().filterNot { it in listUri }
            if (selected.size > remaining) ToastUtils.showToast(requireContext(), getString(R.string.review_invalid))
            selected.take(remaining).forEach { selectedUri ->
                if (ReviewImageUris.keepReadAccess(requireContext(), selectedUri)) listUri.add(selectedUri)
                else ToastUtils.showToast(requireContext(), getString(R.string.review_image_access_error))
            }
            imageDescriptionAdapter.submitList(listUri.toList())
            _binding?.tvNtts2?.isVisible = listUri.isEmpty()
        }
    private val takePictureResult: ActivityResultLauncher<Uri?> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            val captured = uri
            if (result && captured != null && listUri.size + listReviewImages.size < StringConstants.REVIEW_MAX_IMAGES) {
                listUri.add(captured)
                imageDescriptionAdapter.submitList(listUri.toList())
            } else if (captured != null) {
                ReviewImageUris.discardCameraUri(requireContext(), captured)
            }
            uri = null
            _binding?.tvNtts2?.isVisible = listUri.isEmpty()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        draftComment = savedInstanceState?.getString(StringConstants.REVIEW_DRAFT_COMMENT)
        star = savedInstanceState?.getInt(StringConstants.REVIEW_DRAFT_STAR)?.takeIf { it in 1..5 }
        uri = savedInstanceState?.getString(StringConstants.REVIEW_CAMERA_URI)?.let(Uri::parse)
        savedInstanceState?.getStringArrayList(StringConstants.REVIEW_DRAFT_IMAGES)?.mapTo(listUri) { Uri.parse(it) }
        savedInstanceState?.getIntegerArrayList(StringConstants.REVIEW_DELETED_IMAGES)?.let { listDeleteReviewImages.addAll(it) }
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
                ReviewImageUris.discardCameraUri(requireContext(), uri)
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
        draftComment?.let { binding.edtReview.setText(it) }
        imageDescriptionAdapter.submitList(listUri.toList())
        listStar.forEachIndexed { index, selected -> selected.selected = index < (star ?: 0) }
        starAdapter.updateStarFromServer(listStar)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productReviewDetailState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
                                binding.btnEditReview.isEnabled = false
                                binding.tvDelete.isEnabled = false
                            }
                            is StateListener.Error -> {
                                binding.btnEditReview.isEnabled = true
                                binding.btnEditReview.setText(R.string.review_retry)
                                binding.tvDelete.isEnabled = false
                                ToastUtils.showToast(requireContext(), it.e?.message ?: StringConstants.HAS_ERROR)
                            }

                            is StateListener.Success<ProductReview> -> {
                                val productReview = it.result
                                binding.btnEditReview.setText(R.string.save)
                                binding.btnEditReview.isEnabled = !viewModel.busy
                                binding.tvDelete.isEnabled = !viewModel.busy
                                if (detailApplied) return@collectLatest
                                detailApplied = true
                                listReviewImages.clear()
                                listReviewImages.addAll(productReview.images.filterNot { image -> image.reviewImageId in listDeleteReviewImages })
                                binding.tvNtts1.isVisible = listReviewImages.isEmpty()
                                this@MyReviewFragment.star = star ?: productReview.rating
                                binding.apply {
                                    edtReview.setText(draftComment ?: productReview.comment)
                                    listStar.forEachIndexed { index, selected -> selected.selected = index < (star ?: 0) }
                                    starAdapter.updateStarFromServer(listStar)
                                    imageShowAdapter.updateImages(listReviewImages.toList())
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
                                binding.btnEditReview.isEnabled = false
                                binding.tvDelete.isEnabled = false
                                binding.layoutShimmer.frameShimmer.isVisible = true
                                this@MyReviewFragment.isCancelable = false
                            }

                            is StateListener.Error -> {
                                ToastUtils.showToast(requireContext(), it.e?.message ?: StringConstants.HAS_ERROR)
                                binding.btnEditReview.isEnabled = detailApplied
                                binding.tvDelete.isEnabled = detailApplied
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                this@MyReviewFragment.isCancelable = true
                            }

                            is StateListener.Success<String> -> {
                                binding.btnEditReview.isEnabled = detailApplied
                                binding.tvDelete.isEnabled = detailApplied
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                this@MyReviewFragment.isCancelable = true
                                listUri.forEach { image -> ReviewImageUris.discardCameraUri(requireContext(), image) }
                                parentFragmentManager.setFragmentResult(StringConstants.REVIEW_CHANGED, bundleOf())
                                this@MyReviewFragment.dismiss()
                            }
                        }
                    }
                }
                launch {
                    viewModel.deleteReviewState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
                                binding.btnEditReview.isEnabled = false
                                binding.tvDelete.isEnabled = false
                                binding.layoutShimmer.frameShimmer.isVisible = true
                                this@MyReviewFragment.isCancelable = false
                            }

                            is StateListener.Error -> {
                                ToastUtils.showToast(requireContext(), it.e?.message ?: StringConstants.HAS_ERROR)
                                binding.btnEditReview.isEnabled = detailApplied
                                binding.tvDelete.isEnabled = detailApplied
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                this@MyReviewFragment.isCancelable = true
                            }

                            is StateListener.Success<String> -> {
                                binding.btnEditReview.isEnabled = detailApplied
                                binding.tvDelete.isEnabled = detailApplied
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                this@MyReviewFragment.isCancelable = true
                                listUri.forEach { image -> ReviewImageUris.discardCameraUri(requireContext(), image) }
                                parentFragmentManager.setFragmentResult(StringConstants.REVIEW_CHANGED, bundleOf())
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
                if (listUri.size + listReviewImages.size >= StringConstants.REVIEW_MAX_IMAGES) {
                    ToastUtils.showToast(requireContext(), getString(R.string.review_invalid))
                    return@setOnClickListener
                }
                uri = ReviewImageUris.createCameraUri(requireContext())
                if (uri == null) {
                    ToastUtils.showToast(requireContext(), StringConstants.HAS_ERROR)
                    return@setOnClickListener
                }
                try { takePictureResult.launch(uri) }
                catch (_: android.content.ActivityNotFoundException) {
                    uri?.let { ReviewImageUris.discardCameraUri(requireContext(), it) }
                    uri = null
                    ToastUtils.showToast(requireContext(), getString(R.string.review_camera_unavailable))
                }
            }
            layoutAddImage.imgGallery.setOnClickListener {
                resultGallery.launch(arrayOf("image/jpeg", "image/png"))
            }
            btnEditReview.setOnClickListener {
                if (!detailApplied) {
                    reviewId?.takeIf { it > 0 }?.let { viewModel.getProductReviewDetail(it) }
                    return@setOnClickListener
                }
                val comment = edtReview.text.toString().trim()
                val rating = star?.takeIf { it in 1..5 }
                val id = reviewId?.takeIf { it > 0 }
                if (id == null || rating == null || !detailApplied || comment.length > StringConstants.REVIEW_MAX_COMMENT_LENGTH || listUri.size + listReviewImages.size > StringConstants.REVIEW_MAX_IMAGES) {
                    ToastUtils.showToast(requireContext(), getString(R.string.review_invalid))
                    return@setOnClickListener
                }
                viewModel.updateReview(id, comment, rating, listUri, listDeleteReviewImages)
            }
            tvDelete.setOnClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(StringConstants.APP_NAME)
                    .setMessage(R.string.review_delete_question)
                    .setNegativeButton(
                        getString(R.string.review_delete_no)
                    ) { _, _ -> }
                    .setPositiveButton(R.string.review_delete_yes) { _, _ ->
                        reviewId?.let { viewModel.deleteReview(it) }
                    }.create()
                dialog.show()
                dialog.window?.apply {
                    setBackgroundDrawableResource(R.drawable.custom_dialog)
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (detailApplied || draftComment != null) {
            outState.putString(StringConstants.REVIEW_DRAFT_COMMENT, _binding?.edtReview?.text?.toString() ?: draftComment)
        }
        outState.putInt(StringConstants.REVIEW_DRAFT_STAR, star ?: 0)
        outState.putStringArrayList(StringConstants.REVIEW_DRAFT_IMAGES, ArrayList(listUri.map { it.toString() }))
        outState.putIntegerArrayList(StringConstants.REVIEW_DELETED_IMAGES, ArrayList(listDeleteReviewImages))
        outState.putString(StringConstants.REVIEW_CAMERA_URI, uri?.toString())
    }

    override fun onDestroyView() {
        if (detailApplied) draftComment = binding.edtReview.text.toString()
        detailApplied = false
        super.onDestroyView()
        _binding = null
    }
}