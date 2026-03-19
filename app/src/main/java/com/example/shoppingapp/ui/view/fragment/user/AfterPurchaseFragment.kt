package com.example.shoppingapp.ui.view.fragment.user

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentAfterPurchaseBinding
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.AfterPurchaseOrderStatusAdapter
import com.example.shoppingapp.ui.viewmodel.user.AfterPurchaseViewModel
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.CheckNavigation
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ViewPagerUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AfterPurchaseFragment : Fragment() {

    private val TAG = AfterPurchaseFragment::class.simpleName
    private val viewModel: AfterPurchaseViewModel by viewModels()
    private var _binding: FragmentAfterPurchaseBinding? = null
    private val binding
        get() = _binding!!
    private val controller by lazy { findNavController() }
    private val viewPager2: ViewPager2 by lazy { requireActivity().findViewById(R.id.view_pager2) }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(
            R.id.bottom_navigation_view
        )
    }
    private val orderId by lazy { //để string vì có thể có nhiều đơn nên ghép id thành dạng A_B
        arguments?.getString(StringConstants.ORDER_ID)
    }
    private val afterPurchaseOrderStatusAdapter by lazy {
        AfterPurchaseOrderStatusAdapter(emptyList()) { orderID ->
            val bundle = bundleOf(
                StringConstants.ORDER_ID to orderID
            )
            CheckNavigation.navigateTo(
                controller,
                R.id.action_afterPurchaseFragment_to_orderDetailFragment,
                R.id.action_afterPurchaseFragment2_to_orderDetailFragment2,
                bundle
            )
        }
    }
    private val listOrderStatus by lazy { mutableListOf<OrderStatus>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAfterPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderId?.let {
            val orderIds = it.split("_")
            if (orderIds.size > 1) {
                viewModel.getOrderStatus(orderIds.map { oid -> oid.toInt() })
            } else {
                viewModel.getOrderStatus(orderIds[0].toInt())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.orderStatusState.collect {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                Log.e(TAG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success -> {
                                val data = it.result
                                listOrderStatus.add(data)
                                afterPurchaseOrderStatusAdapter.setListStatusOrder(listOrderStatus)
                            }
                        }
                    }
                }
            }
        }

        binding.apply {

            recyclerViewOrderStatus.apply {
                adapter = afterPurchaseOrderStatusAdapter
                layoutManager =
                    LinearLayoutManager(requireContext())
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
                ViewPagerUtils.enableViewPager(viewPager2, controller)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listOrderStatus.clear()
    }

}