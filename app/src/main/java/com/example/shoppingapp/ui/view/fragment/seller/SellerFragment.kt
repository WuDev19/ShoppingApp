package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.FragmentSellerBinding
import com.example.shoppingapp.domain.model.SearchProductShop
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.viewmodel.seller.SellerViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment() {

    private val viewModel: SellerViewModel by viewModels()
    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER)
    }
    private lateinit var isShowing: Fragment
    private val productShopFragment by lazy { ProductShopFragment() }
    private val categoryShopFragment by lazy { CategoryShopFragment() }
    private val bestSellerFragment by lazy { BestSellerFragment() }
    private val productAdapter by lazy {
        ProductAdapter { productId, sellerId ->
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            findNavController().navigate(
                R.id.action_sellerFragment_to_detailProductFragment,
                bundle
            )
        }
    }
    private var isSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idSeller?.let {
            viewModel.getSellerDetail(it)
        } ?: ToastUtils.showToast(requireContext(), "Something went wrong")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabLayout()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var fragment: Fragment? = null
                when (tab?.position) {
                    0 -> {
                        fragment = productShopFragment
                    }

                    1 -> {
                        fragment = categoryShopFragment
                    }

                    2 -> {
                        fragment = bestSellerFragment
                    }
                }
                fragment?.let { hideAndShow(it) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.recyclerViewSearchInShop.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sellerDetailState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> ToastUtils.showToast(
                            requireContext(),
                            it.e.toString()
                        )

                        is StateListener.Success -> {
                            val seller = it.result
                            binding.tvShopName.text = seller.tenShop
                            binding.tvTotalProduct.text = seller.quantityOnSale.toString()
                            Glide.with(binding.root)
                                .load(seller.avtShop)
                                .placeholder(R.drawable.place_holder)
                                .error(R.drawable.icon_avatar)
                                .into(binding.imgAvatar)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchProductShop.collectLatest {
                if (isSearch) {
                    productAdapter.submitData(it)
                }
            }
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // TODO: sau toi uu bang cach them mot man khac de phuc vu viec tim kiem
        binding.edtSearch.addTextChangedListener {
            val text = it.toString()
            isSearch = text.isNotEmpty()
            if (text.isNotEmpty()) {
                binding.recyclerViewSearchInShop.visibility = View.VISIBLE
                binding.linearTab.visibility = View.GONE
                val productSearchShop = SearchProductShop(idSeller!!, text)
                viewModel.setSearchProductShop(productSearchShop)
            } else {
                binding.recyclerViewSearchInShop.visibility = View.GONE
                binding.linearTab.visibility = View.VISIBLE
                productShopFragment.getAdapter()
                    .refresh() //refresh lai cua product shop frag khi ko search nua
            }
        }

    }

    private fun setUpTabLayout() {
        isShowing = productShopFragment

        val tab1 = childFragmentManager.findFragmentByTag("tab1")
        val tab2 = childFragmentManager.findFragmentByTag("tab2")
        val tab3 = childFragmentManager.findFragmentByTag("tab3")

        binding.tabLayout.addTab(binding.tabLayout.newTab().apply {
            text = "Products"
        }, 0, true)
        binding.tabLayout.addTab(binding.tabLayout.newTab().apply {
            text = "Category"
        }, 1, false)
        binding.tabLayout.addTab(binding.tabLayout.newTab().apply {
            text = "Best Seller"
        }, 2, false)

        if (tab1 == null && tab2 == null && tab3 == null) {
            val bundle = bundleOf(
                StringConstants.ID_SELLER to idSeller
            )
            productShopFragment.arguments = bundle
            categoryShopFragment.arguments = bundle
            bestSellerFragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .add(R.id.frame_fragment, productShopFragment, "tab1")
                .add(R.id.frame_fragment, categoryShopFragment, "tab2").hide(categoryShopFragment)
                .add(R.id.frame_fragment, bestSellerFragment, "tab3").hide(bestSellerFragment)
                .commit()
        }
    }

    private fun hideAndShow(fragment: Fragment) {
        if (fragment == isShowing) return
        childFragmentManager.beginTransaction()
            .hide(isShowing)
            .show(fragment)
            .commit()
        isShowing = fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}