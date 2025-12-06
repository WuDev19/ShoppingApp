package com.example.shoppingapp.ui.view.fragment.product

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentCartBinding
import com.example.shoppingapp.ui.view.adapter.CartAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.product.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private val viewModel: CartViewModel by viewModels()
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private val viewPager2: ViewPager2 by lazy { requireActivity().findViewById(R.id.view_pager2) }
    private val bottomNavigationView: BottomNavigationView by lazy { requireActivity().findViewById(R.id.bottom_navigation_view) }
    private val cartAdapter by lazy {
        CartAdapter(viewModel, lifecycleScope)
    }
    private val currentUserId by lazy {
        arguments?.getString(StringConstants.CURRENT_USER_ID) ?: ""
    } // TODO: ve sau chay id nay 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getCartUser("u16").collectLatest {
                cartAdapter.submitData(it)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
            addItemDecoration(MarginItemDecoration(10))
        }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
            BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
            ViewPagerUtils.enableViewPager(viewPager2, controller)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}