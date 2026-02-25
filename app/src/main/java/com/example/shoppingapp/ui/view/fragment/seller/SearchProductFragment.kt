package com.example.shoppingapp.ui.view.fragment.seller

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.databinding.FragmentSearchProductBinding
import com.example.shoppingapp.domain.model.SearchProductShop
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.seller.SearchProductViewModel
import com.example.shoppingapp.utils.CheckNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchProductFragment : Fragment() {

    private val viewModel: SearchProductViewModel by viewModels()
    private var _binding: FragmentSearchProductBinding? = null
    private val binding get() = _binding!!
    private val productAdapter by lazy {
        ProductAdapter { productId, sellerId ->
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            CheckNavigation.navigateTo(
                findNavController(),
                R.id.action_searchProductFragment_to_detailProductFragment,
                R.id.action_searchProductFragment2_to_detailProductFragment2,
                bundle
            )
        }
    }
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            edtSearch.requestFocus()
            val keyboard =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)

            recyclerViewSearchProduct.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = productAdapter
                addItemDecoration(MarginItemDecoration(10))
            }

            edtSearch.addTextChangedListener {
                val text = it.toString()
                val productSearchShop = SearchProductShop(idSeller, text)
                viewModel.setSearchProductShop(productSearchShop)
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchProductShop.collectLatest {
                    Log.e("Searchhh", "onViewCreated: $it")
                    productAdapter.submitData(it)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}