package com.example.shoppingapp.ui.view.fragment.seller

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.databinding.FragmentPostProductBinding
import com.example.shoppingapp.domain.model.Category
import com.example.shoppingapp.ui.view.adapter.CategoryAdapter
import com.example.shoppingapp.ui.view.adapter.ImageDescriptionAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration

//dùng imageDecoder để chuyển uri sang bitmap
class PostProductFragment : Fragment() {
    private var _binding: FragmentPostProductBinding? = null
    private val binding
        get() = _binding!!
    private val listUri: MutableList<Uri?> by lazy { mutableListOf() }
    private var uri: Uri? = null

    //phải submit list mới thì mới cập nhật, nếu submit cùng instance list thì ko cập nhật
    private lateinit var imageDescriptionAdapter: ImageDescriptionAdapter
    private val resultGallery: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
            listUri.addAll(result)
            imageDescriptionAdapter.submitList(listUri.toList())
        }

    private val takePictureResult: ActivityResultLauncher<Uri?> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                listUri.add(uri)
                imageDescriptionAdapter.submitList(listUri.toList())
            }
        }

    private val categories by lazy {
        listOf(
            Category(1, StringConstants.SPORT, false),
            Category(2, StringConstants.FURNITURE, false),
            Category(3, StringConstants.ELECTRONIC, false),
            Category(4, StringConstants.CLOTH, false),
            Category(5, StringConstants.SHOE, false),
            Category(6, StringConstants.COSMETIC, false),
            Category(7, StringConstants.MOBILE, false),
            Category(8, StringConstants.LAPTOP, false),
        )
    }

    private val choseCategory by lazy { mutableListOf<Category>() }

    private lateinit var categoryAdapter: CategoryAdapter

    private val controller by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDescriptionAdapter = ImageDescriptionAdapter(
            callback = {
                val bundle = bundleOf(
                    StringConstants.IMAGE_DESCRIPTION_SLIDER to listUri.map { it.toString() }
                )
                controller.navigate(
                    R.id.action_postProductFragment_to_imageDescriptionProductFragment3,
                    bundle
                )
            },
            delete = { uri ->
                listUri.remove(uri)
                imageDescriptionAdapter.submitList(listUri.toList())
            }
        )
        categoryAdapter = CategoryAdapter(requireActivity(), categories) { category ->
            if(category.selected){
                choseCategory.add(category)
            }
            else {
                choseCategory.remove(category)
            }
        }
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

            buildRecyclerView(this)

            recyclerViewCategory.apply {
                adapter = categoryAdapter
                layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

            imgTakePhoto.setOnClickListener {
                uri = createUri()
                takePictureResult.launch(uri)
            }

            imgGallery.setOnClickListener {
                resultGallery.launch("image/*")
            }

            btAddProductDetail.setOnClickListener {
                moveToProductDetail(this)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createUri(): Uri? {
        // tạo contentvalues để ghi vào csdl của máy
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/ShoppingApp"
            )
        }
        return requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // luu vao bo nho dung chung
            contentValues
        )
    }

    private fun buildRecyclerView(binding: FragmentPostProductBinding) {
        binding.apply {

            recyclerViewImage.apply {
                adapter = imageDescriptionAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
                addItemDecoration(MarginItemDecoration(5))
            }

            recyclerViewImage.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(
                    rv: RecyclerView,
                    e: MotionEvent
                ): Boolean {
                    rv.parent.requestDisallowInterceptTouchEvent(true)
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
    }

    private fun moveToProductDetail(binding: FragmentPostProductBinding) {
        binding.apply {
            Log.e(StringConstants.DEBUG, "onViewCreated: ${listUri.toTypedArray()}")
            val description = edtDescription.text.toString().trim()
            val price = edtProductPrice.text.toString().trim()
            val discount =
                if (edtDiscount.text.toString().isNotEmpty()) edtDiscount.text.toString().trim()
                    .toInt() else 0
            val introduction = if (edtIntroduction.text.toString()
                    .isNotEmpty()
            ) edtIntroduction.text.toString().trim() else null

            if (description.isEmpty() || price.isEmpty() || listUri.isEmpty()) {
                ToastUtils.showToast(requireContext(), "Vui lòng điềm đầy đủ thông tin")
                return
            }
            val uriList = listUri.map { uri -> uri.toString() }
            val action =
                PostProductFragmentDirections.actionPostProductFragmentToPostProductDetailFragment(
                    description = description,
                    generalPrice = price.toInt(),
                    introduction = introduction,
                    discount = discount,
                    uriList = uriList.toTypedArray(),
                )
            clearDataOnView(binding)
            controller.navigate(action)
        }
    }

    private fun clearDataOnView(binding: FragmentPostProductBinding) {
        binding.apply {
            edtDescription.setText("")
            edtProductPrice.setText("")
            edtDiscount.setText("")
            edtIntroduction.setText("")
            imageDescriptionAdapter.submitList(emptyList())
        }
    }

}