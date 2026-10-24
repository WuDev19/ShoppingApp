package com.example.shoppingapp.ui.view.fragment.user

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentWriteReviewBinding
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.model.SelectedStar
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ImageDescriptionAdapter
import com.example.shoppingapp.ui.view.adapter.StarAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.user.WriteReviewViewModel
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.utils.ReviewImageUris
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
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
        StarAdapter(listStar = listStar) { star ->
            this.star = star
        }
    }
    private val orderItemId by lazy {
        arguments?.getInt(StringConstants.ORDER_ITEM_ID)
    }
    private val productId by lazy {
        arguments?.getInt(StringConstants.ID_PRODUCT)
    }
    private val listUri: MutableList<Uri> by lazy { mutableListOf() }
    private var uri: Uri? = null
    private lateinit var imageDescriptionAdapter: ImageDescriptionAdapter
    private val controller by lazy {
        requireParentFragment().findNavController()
    }
    private var draftComment = ""
    private var star: Int? = null
    private val resultGallery: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { result ->
            val remaining = (StringConstants.REVIEW_MAX_IMAGES - listUri.size).coerceAtLeast(0)
            val selected = result.distinct().filterNot { it in listUri }
            if (selected.size > remaining) ToastUtils.showToast(requireContext(), getString(R.string.review_invalid))
            selected.take(remaining).forEach { selectedUri ->
                if (ReviewImageUris.keepReadAccess(requireContext(), selectedUri)) listUri.add(selectedUri)
                else ToastUtils.showToast(requireContext(), getString(R.string.review_image_access_error))
            }
            imageDescriptionAdapter.submitList(listUri.toList())
        }
    private val takePictureResult: ActivityResultLauncher<Uri?> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            val captured = uri
            if (result && captured != null && listUri.size < StringConstants.REVIEW_MAX_IMAGES) {
                listUri.add(captured)
                imageDescriptionAdapter.submitList(listUri.toList())
            } else if (captured != null) {
                ReviewImageUris.discardCameraUri(requireContext(), captured)
            }
            uri = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        star = savedInstanceState?.getInt(StringConstants.REVIEW_DRAFT_STAR)?.takeIf { it in 1..5 }
        savedInstanceState?.getStringArrayList(StringConstants.REVIEW_DRAFT_IMAGES)?.mapTo(listUri) { Uri.parse(it) }
        uri = savedInstanceState?.getString(StringConstants.REVIEW_CAMERA_URI)?.let(Uri::parse)
        draftComment = savedInstanceState?.getString(StringConstants.REVIEW_DRAFT_COMMENT).orEmpty()
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
            }
        )
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
        binding.edtComment.setText(draftComment)
        listStar.forEachIndexed { index, selected -> selected.selected = index < (star ?: 0) }
        imageDescriptionAdapter.submitList(listUri.toList())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postReviewState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> {
                            binding.btnPostComment.isEnabled = false
                            binding.layoutShimmer.frameShimmer.isVisible = true
                            this@WriteReviewFragment.isCancelable = false
                        }

                        is StateListener.Error -> {
                            binding.btnPostComment.isEnabled = true
                            binding.layoutShimmer.frameShimmer.isVisible = false
                            this@WriteReviewFragment.isCancelable = true
                            ToastUtils.showToast(
                                requireContext(),
                                it.e?.message ?: StringConstants.HAS_ERROR
                            )
                        }

                        is StateListener.Success<Unit> -> {
                            listUri.forEach { image -> ReviewImageUris.discardCameraUri(requireContext(), image) }
                            parentFragmentManager.setFragmentResult(StringConstants.REVIEW_CHANGED, bundleOf())
                            ToastUtils.showToast(requireContext(), getString(R.string.review_success))
                            binding.btnPostComment.isEnabled = true
                            binding.layoutShimmer.frameShimmer.isVisible = false
                            this@WriteReviewFragment.isCancelable = true
                            this@WriteReviewFragment.dismiss()
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

            layoutAddImage.recyclerViewImage.apply {
                adapter = imageDescriptionAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
                addItemDecoration(MarginItemDecoration(5))
            }

            layoutAddImage.imgTakePhoto.setOnClickListener {
                if (listUri.size >= StringConstants.REVIEW_MAX_IMAGES) {
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

            btnPostComment.setOnClickListener {
                val product = productId?.takeIf { it > 0 }
                val orderItem = orderItemId?.takeIf { it > 0 }
                val rating = star?.takeIf { it in 1..5 }
                val comment = edtComment.text.toString().trim()
                if (product == null || orderItem == null || rating == null || comment.length > StringConstants.REVIEW_MAX_COMMENT_LENGTH || listUri.size > StringConstants.REVIEW_MAX_IMAGES) {
                    ToastUtils.showToast(requireContext(), getString(R.string.review_invalid))
                    return@setOnClickListener
                }
                val createReview = CreateReview(null, product, orderItem, rating,
                    comment.ifEmpty { null }, emptyList())
                viewModel.postReview(createReview, listUri)
            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(StringConstants.REVIEW_DRAFT_STAR, star ?: 0)
        outState.putString(StringConstants.REVIEW_DRAFT_COMMENT, _binding?.edtComment?.text?.toString() ?: draftComment)
        outState.putStringArrayList(StringConstants.REVIEW_DRAFT_IMAGES, ArrayList(listUri.map { it.toString() }))
        outState.putString(StringConstants.REVIEW_CAMERA_URI, uri?.toString())
    }

    override fun onDestroyView() {
        draftComment = binding.edtComment.text.toString()
        super.onDestroyView()
        _binding = null
    }

}