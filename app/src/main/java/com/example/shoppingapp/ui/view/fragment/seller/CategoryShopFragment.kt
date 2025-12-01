package com.example.shoppingapp.ui.view.fragment.seller

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.FragmentCategoryShopBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.CategoryQuantityAdapter
import com.example.shoppingapp.ui.viewmodel.seller.CategoryShopViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryShopFragment : Fragment() {

    private val viewModel: CategoryShopViewModel by viewModels()
    private var _binding: FragmentCategoryShopBinding? = null
    private val binding get() = _binding!!
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER) ?: -1
    }
    private val categoryQuantityAdapter by lazy {
        CategoryQuantityAdapter {categoryName ->
            val bundle = bundleOf(
                StringConstants.ID_SELLER to idSeller,
                StringConstants.CATEGORY_NAME to categoryName
            )
            findNavController().navigate(R.id.action_sellerFragment_to_categoryProductDetailShopFragment, bundle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getDanhMucVaSl(idSeller)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewCategoryShop.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryQuantityAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateCateAndQuantity.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> ToastUtils.showToast(requireContext(), it.e.toString())
                        is StateListener.Success -> {
                            categoryQuantityAdapter.submitList(it.result)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}