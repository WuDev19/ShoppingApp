package com.example.shoppingapp.ui.view.fragment.seller

import android.content.ContentValues
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import com.example.shoppingapp.databinding.FragmentPostProductDetailBinding
import com.example.shoppingapp.ui.viewmodel.seller.PostProductDetailViewModel
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.data.remote.dto.request.PostProductDetailRequest
import com.example.shoppingapp.data.remote.dto.request.PostProductRequest
import com.example.shoppingapp.domain.model.UploadingModel
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.UploadImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PostProductDetailFragment : Fragment() {

    private val viewModel: PostProductDetailViewModel by viewModels()
    private var _binding: FragmentPostProductDetailBinding? = null
    private val binding
        get() = _binding!!
    private val args: PostProductDetailFragmentArgs by navArgs()
    private val description
        get() = args.description
    private val generalPrice
        get() = args.generalPrice
    private val discount
        get() = args.discount
    private val introduction
        get() = args.introduction
    private val uriList by lazy {
        args.uriList.map {
            it.toUri()
        }
    }
    private val listImageDetail by lazy { mutableListOf<Uri?>() }
    private var uri: Uri? = null
    private var oldUri: Uri? = null
    private var count: Int = 0
    private val listPostProductDetailRequest by lazy { mutableListOf<PostProductDetailRequest>() }
    private val galleryResult: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        result?.let {
            uri = it
            listImageDetail.add(it)
            Glide.with(requireContext())
                .load(it)
                .error(R.drawable.icon_avatar)
                .into(binding.imgProductImage)
        }
    }
    private val takePhotoResult: ActivityResultLauncher<Uri?> = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            listImageDetail.add(uri)
            Glide.with(requireContext())
                .load(uri)
                .error(R.drawable.icon_avatar)
                .into(binding.imgProductImage)
        }
    }
    private val listUploadModel by lazy { mutableListOf<UploadingModel>() }
    private val uploadImageAdapter by lazy { UploadImageAdapter(listUploadModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.postProductState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {

                            }

                            is StateListener.Error -> {

                            }

                            is StateListener.Success<Int> -> {

                            }
                        }
                    }
                }

                launch {
                    viewModel.numberImageSuccess.collect {
                        if (it > 0) {
                            Log.e(StringConstants.DEBUG, "onViewCreated: thanh cong $it")
                        }
                    }
                }

                launch {
                    viewModel.uploadImageDetailState.collect {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {}
                            is StateListener.Error -> {}
                            is StateListener.Success<PostProductDetailRequest> -> {
                                listPostProductDetailRequest.add(it.result)
                                ToastUtils.showToast(requireContext(), "Save Successfully")
                            }
                        }
                    }
                }

                launch {
                    viewModel.uploadingManager.uploadPercent.collectLatest {
                        uploadImageAdapter.update(it.position, it)
                    }
                }
            }
        }

        binding.apply {

            recyclerViewDownload.apply {
                adapter = uploadImageAdapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

            btnSaveTmp.setOnClickListener {
                val productNameDetail = edtProductName.text.toString().trim()
                val inStock = edtInStock.text.toString().trim()
                val unitPrice = edtUnitPrice.text.toString().trim()
                val color = edtColor.text.toString().trim()
                val size = edtSize.text.toString().trim()
                if (productNameDetail.isEmpty() ||
                    inStock.isEmpty() ||
                    unitPrice.isEmpty() ||
                    uri == null ||
                    description.isEmpty() ||
                    generalPrice == -1 ||
                    uriList.isEmpty()
                ) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Information is missing, please review and fill it in completely"
                    )
                    return@setOnClickListener
                } else if (uri == oldUri) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Please renew product"
                    )
                    return@setOnClickListener
                }
                val postProductDetailRequest = PostProductDetailRequest(
                    productNameDetail,
                    inStock.toInt(),
                    null,
                    unitPrice.toInt(),
                    color.ifEmpty { null },
                    size.ifEmpty { null }
                )
                count++
                val uploadingModel = UploadingModel(listUploadModel.size - 1 + 1, null, null, uri!!)
                uploadImageAdapter.addItem(uploadingModel)
                viewModel.uploadImageProductDetail(uri!!, postProductDetailRequest, uploadingModel)
            }

            btnPost.setOnClickListener {
                if (count != listPostProductDetailRequest.size) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Please wait the upload image process"
                    )
                    return@setOnClickListener
                }
                if (listPostProductDetailRequest.isEmpty() ||
                    description.isEmpty() ||
                    generalPrice == -1 ||
                    uriList.isEmpty()
                ) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Information is missing, please review and fill it in completely"
                    )
                    return@setOnClickListener
                }

                val postProductRequest = PostProductRequest(
                    generalPrice,
                    description,
                    null,
                    if (discount == -1) null else discount,
                    introduction,
                    listPostProductDetailRequest,
                    null
                )
                viewModel.postProduct(uriList, postProductRequest)
            }

            imgTakePhoto.setOnClickListener {
                uri = createUriToSave()
                takePhotoResult.launch(uri)
            }

            imgGallery.setOnClickListener {
                galleryResult.launch("image/*")
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createUriToSave(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

}