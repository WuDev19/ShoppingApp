package com.example.shoppingapp.ui.view.fragment.product

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.CheckNavigation
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.utils.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentDetailProductBinding
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.product.ProductDetail
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.GridAdapterInsideConcatAdapter
import com.example.shoppingapp.ui.view.adapter.HeaderDetailAdapter
import com.example.shoppingapp.ui.view.adapter.HorizontalAdapterInsideConcatAdapter
import com.example.shoppingapp.ui.view.adapter.OtherProductOfShopAdapter
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.view.adapter.TitleDetailAdapter
import com.example.shoppingapp.ui.viewmodel.product.DetailProductViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private val idProduct by lazy {
        arguments?.getInt(StringConstants.ID_PRODUCT) ?: -1
    }
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER) ?: -1
    }
    private val viewModel: DetailProductViewModel by viewModels()
    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(R.id.bottom_navigation_view)
    }
    private val viewPager2: ViewPager2 by lazy {
        requireActivity().findViewById(R.id.view_pager2)
    }
    private val otherProductOfShopAdapter: OtherProductOfShopAdapter by lazy {
        OtherProductOfShopAdapter { productId, sellerId ->
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            CheckNavigation.navigateTo(
                controller,
                R.id.action_detailProductFragment_self,
                R.id.action_detailProductFragment2_self,
                bundle
            )
        }
    }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter { productId, sellerId ->
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            CheckNavigation.navigateTo(
                controller,
                R.id.action_detailProductFragment_self,
                R.id.action_detailProductFragment2_self,
                bundle
            )
        }
    }
    private lateinit var productDetail: ProductDetail

    @Inject
    @Named("userId")
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getProductDetail(idProduct)

        lifecycleScope.launch {
            launch {
                viewModel.getOtherProductsOfShop(idSeller).collectLatest {
                    otherProductOfShopAdapter.submitData(it)
                }
            }
            launch {
                viewModel.getSanPhamRelated(idProduct).collectLatest {
                    productAdapter.submitData(it)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerDetailAdapter =
            HeaderDetailAdapter(
                onImageClick = { it1 ->
                    val bundle = bundleOf(
                        StringConstants.IMAGE_DESCRIPTION_SLIDER to it1
                    )
                    CheckNavigation.navigateTo(
                        controller,
                        R.id.action_detailProductFragment_to_imageDescriptionProductFragment,
                        R.id.action_detailProductFragment2_to_imageDescriptionProductFragment2,
                        bundle
                    )
                },
                onShowShopClick = { it1 ->
                    val bundle = bundleOf(
                        StringConstants.ID_SELLER to it1
                    )
                    CheckNavigation.navigateTo(
                        controller,
                        R.id.action_detailProductFragment_to_sellerFragment,
                        R.id.action_detailProductFragment2_to_sellerFragment2,
                        bundle
                    )
                }
            )
        val concatAdapter = ConcatAdapter(
            headerDetailAdapter,
            TitleDetailAdapter(
                "",
                StringConstants.TITLE_OTHER_PRODUCT,
                "",
                null,
                null
            ),
            HorizontalAdapterInsideConcatAdapter(otherProductOfShopAdapter),
            TitleDetailAdapter(
                "4.9",
                StringConstants.TITLE_PRODUCT_REVIEWS,
                StringConstants.ALL,
                idProduct
            ) { it1 ->
                Log.e(StringConstants.DEBUG, "onViewCreated: $it1")
            },
            TitleDetailAdapter(
                "",
                StringConstants.TITLE_RELATED_PRODUCT,
                "",
                null,
                null
            ),
            GridAdapterInsideConcatAdapter(productAdapter)
        )

        // TODO: toi uu lai concat adapter thoi
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productDetailStateFLow.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> {
                            ToastUtils.showToast(requireContext(), it.e.toString())
                        }

                        is StateListener.Success -> {
                            productDetail = it.result
                            headerDetailAdapter.setProductDetail(productDetail)
                        }
                    }
                }
            }

        }

        binding.apply {
            recyclerViewConcat.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = concatAdapter
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
                BottomNavigationUtils.showBottomNavigation(
                    bottomNavigationView,
                    controller
                ) // goi o day tai vi fragment nay chi dung sau moi home hoac store
                ViewPagerUtils.enableViewPager(viewPager2, controller)
            }

            imgAddToCart.setOnClickListener {
                showProductDetail(false)
            }

            imgBuy.setOnClickListener {
                showProductDetail(true)
            }
        }

    }

    private fun showProductDetail(isBuy: Boolean) {
        val addToCartFragment = AddToCartFragment { productChiTiet, number ->
            val orderFromBuyNow =
                OrderFromBuyNow(
                    userId,
                    productDetail.maNguoiBan!!,
                    productDetail.tenNguoiBan!!,
                    productDetail.provinceId!!,
                    productDetail.districtId!!,
                    productDetail.wardCode!!,
                    productDetail.shopGhnId!!,
                    number,
                    productDetail.giamgia ?: 0,
                    productChiTiet
                )
            val bundle = bundleOf(
                StringConstants.ORDER_BUY_NOW to orderFromBuyNow
            )
            CheckNavigation.navigateTo(
                controller,
                R.id.action_detailProductFragment_to_purchaseProductFragment,
                R.id.action_detailProductFragment2_to_purchaseProductFragment2,
                bundle
            )
        }
        val bundle = bundleOf(
            StringConstants.ID_PRODUCT to idProduct,
            StringConstants.DISCOUNT to productDetail.giamgia,
            StringConstants.ID_SELLER to idSeller,
            StringConstants.IS_BUY to isBuy
        )
        addToCartFragment.arguments = bundle
        addToCartFragment.show(
            requireActivity().supportFragmentManager,
            addToCartFragment.tag
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}