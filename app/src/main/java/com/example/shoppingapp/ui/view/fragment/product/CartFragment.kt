package com.example.shoppingapp.ui.view.fragment.product

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.CheckNavigation
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentCartBinding
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.CartUiModel
import com.example.shoppingapp.domain.model.OrderFromCart
import com.example.shoppingapp.ui.view.adapter.CartAdapter
import com.example.shoppingapp.ui.viewmodel.product.CartViewModel
import com.example.shoppingapp.utils.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private val viewModel: CartViewModel by viewModels()
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private val viewPager2: ViewPager2 by lazy { requireActivity().findViewById(R.id.view_pager2) }
    private val listCart by lazy { mutableSetOf<CartUiModel>() }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(
            R.id.bottom_navigation_view
        )
    }
    private lateinit var cartAdapter: CartAdapter
    private var isEdit: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartAdapter = CartAdapter(
            onCheckBoxItem = { model, isChecked ->
                if (isChecked) {
                    listCart.add(model)
                } else {
                    listCart.remove(model)
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getCartUser().collectLatest {
                        cartAdapter.submitData(it)
                    }
                }
            }
        }

        binding.apply {
            recyclerViewCart.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = cartAdapter
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
                ViewPagerUtils.enableViewPager(viewPager2, controller)
            }

            btnBuy.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (listCart.isEmpty()) {
                        ToastUtils.showToast(requireContext(), "Please select at least one item")
                        return@launch
                    }
                    progressBar.isVisible = true
                    tvWait.isVisible = true
                    shimmer.isVisible = true
                    linearMain.isEnabled = false
                    val list = async(Dispatchers.Default) {
                        handleCartToPurchase()
                    }.await()
                    delay(1000)
                    progressBar.isVisible = false
                    tvWait.isVisible = false
                    shimmer.isVisible = false
                    linearMain.isEnabled = true
                    listCart.clear()
                    val bundle = bundleOf(
                        StringConstants.LIST_ORDER to (list)
                    )
                    CheckNavigation.navigateTo(
                        controller,
                        R.id.action_cartFragment_to_purchaseProductFragment,
                        R.id.action_cartFragment2_to_purchaseProductFragment2,
                        bundle
                    )
                }
            }

            tvEdit.setOnClickListener {
                if (!isEdit) {
                    isEdit = true
                    btnDelete.isVisible = true
                } else {
                    isEdit = false
                    btnDelete.isVisible = false
                }
            }
        }

    }

    private fun handleCartToPurchase(): List<OrderFromCart> {
        val result = mutableSetOf<OrderFromCart>()
        val snapshotAdapter = cartAdapter.snapshot().items
        for (item in snapshotAdapter) {
            if (item is Cart) {
                var check = false
                val orderFromCart = OrderFromCart()
                val cartItems = mutableListOf<CartItem>()
                for (cartItem in listCart) {
                    if (cartItem is CartItem && cartItem.cartId == item.cartId) {
                        cartItems.add(cartItem)
                        check = true
                    }
                }
                if (check) {
                    orderFromCart.cart = item
                    orderFromCart.cartItems = cartItems
                    result.add(orderFromCart)
                }
            }
        }
        return ArrayList<OrderFromCart>(result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}