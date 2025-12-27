package com.example.shoppingapp.ui.view.fragment.product

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Paint
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.data.remote.dto.request.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.dto.request.CartItemRequest
import com.example.shoppingapp.data.remote.dto.request.CartRequest
import com.example.shoppingapp.databinding.CustomDialogImageDetailBinding
import com.example.shoppingapp.databinding.FragmentAddToCartBinding
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import com.example.shoppingapp.domain.model.SizeColorModel
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.SizeAndColorAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.product.AddToCartViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

@AndroidEntryPoint
class AddToCartFragment : BottomSheetDialogFragment() {

    private val viewModel: AddToCartViewModel by viewModels()
    private var _binding: FragmentAddToCartBinding? = null
    private val binding get() = _binding!!
    private val idProduct by lazy {
        arguments?.getInt(StringConstants.ID_PRODUCT)
    }
    private val giamGia by lazy {
        arguments?.getInt(StringConstants.DISCOUNT) ?: 0
    }
    private val isdSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER)
    }
    private lateinit var productChiTiet: ProductChiTiet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSanPhamChiTiet(idProduct!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    // TODO: case neu chi co color hoac size cung phai xu ly
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sanPhamChiTietState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> {
                            ToastUtils.showToast(requireContext(), it.e.toString())
                        }

                        is StateListener.Success -> {
                            val data = it.result //danh sach san pham chi tiet trong 1 san pham
                            withContext(Dispatchers.Default) {
                                val listColor = data.map { it1 ->
                                    SizeColorModel(it1.color, false)
                                }.distinct()
                                withContext(Dispatchers.Main) {
                                    if (listColor.map { it3 -> it3.data }.contains(null)) {
                                        binding.recyclerViewColor.visibility = View.GONE
                                        binding.tvColor.visibility = View.GONE
                                    } else {
                                        binding.recyclerViewColor.apply {
                                            layoutManager = FlexboxLayoutManager(
                                                requireContext(),
                                                FlexDirection.ROW,
                                                FlexWrap.WRAP,
                                            )
                                            adapter =
                                                SizeAndColorAdapter(listColor) { data, selected ->
                                                    if (selected) {
                                                        viewModel.selectedColor.value = data
                                                    } else {
                                                        viewModel.selectedColor.value = ""
                                                    }
                                                }
                                            addItemDecoration(MarginItemDecoration(5))
                                        }
                                    }
                                }
                            }
                            withContext(Dispatchers.Default) {
                                val listSize = data.map { it1 ->
                                    SizeColorModel(it1.kichco, false)
                                }.distinct()
                                withContext(Dispatchers.Main) {
                                    if (listSize.map { it3 -> it3.data }.contains(null)) {
                                        binding.recyclerViewSize.visibility = View.GONE
                                        binding.tvSize.visibility = View.GONE
                                    } else {
                                        binding.recyclerViewSize.apply {
                                            layoutManager = FlexboxLayoutManager(
                                                requireContext(),
                                                FlexDirection.ROW,
                                                FlexWrap.WRAP
                                            )
                                            adapter =
                                                SizeAndColorAdapter(listSize) { data, selected ->
                                                    if (selected) {
                                                        viewModel.selectedSize.value = data
                                                    } else {
                                                        viewModel.selectedSize.value = ""
                                                    }
                                                }
                                            addItemDecoration(MarginItemDecoration(5))
                                        }
                                    }
                                }
                            }
                            viewModel.displayCorrespondingProduct.collectLatest { it1 ->
                                val list = it1.split("_")
                                if (list[0].isEmpty() || list[1].isEmpty()) { //dau tien khi bam vao se hien item dau
                                    bindingProduct(data[0])
                                    disableBtnAddCart()
                                } else {
                                    //tim kiem trong data xem cai nao dau tien co color voi kich co = cai minh chon tren view
                                    val kq = data.firstOrNull { it2 ->
                                        it2.color == list[1] && it2.kichco == list[0]
                                    }
                                    if (kq != null) {
                                        productChiTiet = kq
                                        bindingProduct(kq)
                                        enableAddCart()
                                    } else {
                                        disableBtnAddCart()
                                        Glide.with(binding.root)
                                            .load(R.drawable.icon_not_found)
                                            .error(R.drawable.icon_not_found)
                                            .into(binding.imgAnhDetail)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createCart.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> {
                            ToastUtils.showToast(requireContext(), it.e.toString())
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                        }

                        is StateListener.Success -> {
                            val response = it.result
                            Log.e(StringConstants.DEBUG, "onViewCreated: $response")
                            this@AddToCartFragment.dismiss()
                        }
                    }
                }
            }
        }

        binding.imgMinus.setOnClickListener {
            var number = binding.edtNumber.text.toString().toInt()
            number = max(1, --number)
            binding.edtNumber.setText(number.toString())
        }

        binding.imgPlus.setOnClickListener {
            var number = binding.edtNumber.text.toString().toInt()
            number++
            binding.edtNumber.setText(number.toString())
        }

        binding.btnAddToCart.setOnClickListener {
            val number = binding.edtNumber.text.toString().toInt()
            if (number > productChiTiet.inStock!!) {
                ToastUtils.showToast(requireContext(), "Exceed the quantity in stock")
                return@setOnClickListener
            }
            val cartRequest = CartRequest(isdSeller!!, "u16")
            val cartItemRequest = CartItemRequest(productChiTiet.maSanPhamDeTail!!, number, giamGia)
            viewModel.createCart(CartAndCartItemRequest(cartRequest, cartItemRequest))
        }

        binding.imgCancel.setOnClickListener {
            dismiss()
        }

        binding.imgAnhDetail.setOnClickListener {
            Log.e("ANHDETAIL", "onViewCreated: ${productChiTiet.anhDetail}")
            val dialogView = CustomDialogImageDetailBinding.inflate(layoutInflater)
            Glide.with(requireContext())
                .load(productChiTiet.anhDetail ?: R.drawable.icon_avatar)
                .error(R.drawable.place_holder)
                .into(dialogView.imgAnh)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView.root)
                .create()
            //phai show dialog truoc roi chinh window thi moi co hieu luc
            dialog.show()
            dialog.window?.apply {
                setDimAmount(0.5f)
                setLayout(
                    (resources.displayMetrics.widthPixels * 0.8).toInt(),
                    (resources.displayMetrics.heightPixels * 0.6).toInt()
                )
                setGravity(Gravity.CENTER)
                setBackgroundDrawableResource(R.drawable.custom_dialog)
            }
        }

    }

    private fun bindingProduct(productChiTiet: ProductChiTiet) {
        if (giamGia != 0) {
            binding.tvPrice.text = buildString {
                append(productChiTiet.unitPrice)
                append(StringConstants.PRICE)
            }
            binding.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            val priceWithDiscount = productChiTiet.unitPrice?.minus(((giamGia.let {
                productChiTiet.unitPrice?.times(
                    it
                )
            })?.div(100)!!))
            binding.tvPriceWithDiscount.text = buildString {
                append(priceWithDiscount)
                append(StringConstants.PRICE)
            }
        } else {
            binding.tvPrice.text = buildString {
                append(productChiTiet.unitPrice)
                append(StringConstants.PRICE)
            }
        }
        binding.tvInStock.text = buildString {
            append(StringConstants.STOCK)
            append(productChiTiet.inStock)
        }
        Glide.with(binding.root)
            .load(productChiTiet.anhDetail)
            .placeholder(R.drawable.place_holder)
            .error(R.drawable.icon_mobile)
            .into(binding.imgAnhDetail)
    }

    private fun disableBtnAddCart() {
        binding.imgAnhDetail.isEnabled = false
        binding.btnAddToCart.isEnabled = false
        binding.btnAddToCart.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_blue
            )
        )
    }

    private fun enableAddCart() {
        binding.imgAnhDetail.isEnabled = true
        binding.btnAddToCart.isEnabled = true
        binding.btnAddToCart.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.button_color
            )
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            dialog.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            )
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}