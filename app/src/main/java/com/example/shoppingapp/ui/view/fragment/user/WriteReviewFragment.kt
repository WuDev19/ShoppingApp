package com.example.shoppingapp.ui.view.fragment.user

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
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
import com.example.shoppingapp.utils.UriHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WriteReviewFragment : BottomSheetDialogFragment() {
    private val TAG by lazy { WriteReviewFragment::class.simpleName }
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
    private var star: Int? = null
    private val resultGallery: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
            listUri.addAll(result)
            imageDescriptionAdapter.submitList(listUri.toList())
        }
    private val takePictureResult: ActivityResultLauncher<Uri?> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                listUri.add(uri!!)
                imageDescriptionAdapter.submitList(listUri.toList())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postReviewState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> {
                            binding.layoutShimmer.frameShimmer.isVisible = true
                            this@WriteReviewFragment.isCancelable = false
                        }

                        is StateListener.Error -> {
                            Log.e(TAG, "onViewCreated: ${it.e?.message}")
                            binding.layoutShimmer.frameShimmer.isVisible = false
                            this@WriteReviewFragment.isCancelable = true
                            ToastUtils.showToast(
                                requireContext(),
                                it.e?.message ?: StringConstants.HAS_ERROR
                            )
                        }

                        is StateListener.Success<Unit> -> {
                            ToastUtils.showToast(requireContext(), "Post successfully")
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

            btnPostComment.setOnClickListener {
                if (productId == null || orderItemId == null) return@setOnClickListener
                val comment = edtComment.text.toString().trim()
                val createReview = CreateReview(
                    null, productId!!, orderItemId!!, star,
                    comment.ifEmpty { null }, emptyList()
                )
                viewModel.postReview(createReview, listUri)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}