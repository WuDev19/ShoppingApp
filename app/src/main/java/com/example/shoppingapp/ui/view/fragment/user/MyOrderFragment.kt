package com.example.shoppingapp.ui.view.fragment.user

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.databinding.FragmentMyOrderBinding
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.model.StatusOrder
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.OrderViaStatusAdapter
import com.example.shoppingapp.ui.view.adapter.StatusOrderAdapter
import com.example.shoppingapp.ui.viewmodel.user.MyOrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyOrderFragment : Fragment() {

    private val TAG = MyOrderFragment::class.simpleName
    private val viewModel: MyOrderViewModel by viewModels()
    private var _binding: FragmentMyOrderBinding? = null
    private val binding
        get() = _binding!!
    private val statusOrderAdapter by lazy {
        StatusOrderAdapter(
            listOf(
                StatusOrder(1, "Pending Payment", true),
                StatusOrder(2, "Await Confirmation", false),
                StatusOrder(3, "Processing", false),
                StatusOrder(4, "Shipping", false),
                StatusOrder(5, "Completed", false),
                StatusOrder(6, "Cancelled", false),
            )
        ) { status, isSelected ->
            if (!isSelected) {
                Log.e(TAG, "click: ")
                viewModel.getOrderViaStatus(status)
            }
        }
    }
    private val orderViaStatusAdapter by lazy {
        OrderViaStatusAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getOrderViaStatus("Pending Payment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderStatusState.collectLatest {
                    orderViaStatusAdapter.submitData(it)
                }
            }

        }

        binding.apply {
            recyclerViewStatusOrder.apply {
                adapter = statusOrderAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            recyclerViewOrder.apply {
                adapter = orderViaStatusAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}