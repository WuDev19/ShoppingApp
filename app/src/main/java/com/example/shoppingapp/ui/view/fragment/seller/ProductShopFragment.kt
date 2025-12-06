package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.CheckNavigation
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.FragmentProductShopBinding
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.seller.ProductShopViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductShopFragment : Fragment() {

    private val viewModel: ProductShopViewModel by viewModels()
    private var _binding: FragmentProductShopBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER)
    }
    private val productAdapter by lazy {
        ProductAdapter { productId, sellerId ->
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            CheckNavigation.navigateTo(
                controller,
                R.id.action_sellerFragment_to_detailProductFragment,
                R.id.action_sellerFragment2_to_detailProductFragment2,
                bundle
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            idSeller?.let {
                viewModel.getAllProductOfShop(it).collectLatest { pagingData ->
                    productAdapter.submitData(pagingData)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewProductOfStore.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            addItemDecoration(MarginItemDecoration(5))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}