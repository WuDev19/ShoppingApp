package com.example.shoppingapp.ui.view.fragment.seller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.databinding.FragmentCategoryProductDetailShopBinding
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.seller.CategoryProductDetailShopViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryProductDetailShopFragment : Fragment() {

    private val viewModel: CategoryProductDetailShopViewModel by viewModels()
    private var _binding: FragmentCategoryProductDetailShopBinding? = null
    private val binding get() = _binding!!
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER) ?: -1
    }
    private val categoryName by lazy {
        arguments?.getString(StringConstants.CATEGORY_NAME) ?: ""
    }
    private val numberProduct by lazy {
        arguments?.getInt(StringConstants.NUMBER_PRODUCT) ?: 0
    }
    private val productAdapter by lazy {
        ProductAdapter{ productId, sellerId ->
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            findNavController().navigate(
                R.id.action_categoryProductDetailShopFragment_to_detailProductFragment,
                bundle
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getCategoryAndProductShop(idSeller, categoryName).collectLatest {
                productAdapter.submitData(it)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryProductDetailShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewDetailCategoryProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            addItemDecoration(MarginItemDecoration(10))
        }

        binding.tvCategory.text = buildString {
            append(categoryName)
            append(":")
        }
        binding.tvNumber.text = numberProduct.toString()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}