package com.example.shoppingapp.ui.view.fragment.user

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentOrderDetailBinding
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.OrderItemAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.user.OrderDetailViewModel
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private val TAG = OrderDetailFragment::class.simpleName
    private val viewModel: OrderDetailViewModel by viewModels()
    private var _binding: FragmentOrderDetailBinding? = null
    private val binding
        get() = _binding!!
    private val orderId by lazy {
        arguments?.getInt(StringConstants.ORDER_ID)
    }
    private val orderItemAdapter by lazy {
        OrderItemAdapter(emptyList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderId?.let {
            viewModel.getOrderDetail(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderDetailState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> {
                            Log.e(TAG, "onViewCreated: ${it.e?.message}")
                        }

                        is StateListener.Success<OrderDetail> -> {
                            val orderDetail = it.result
                            binding.apply {
                                tvTotal.text = buildString {
                                    append(orderDetail.totalAmount.toString())
                                    append("$")
                                }
                                tvTotal.setTypeface(null, Typeface.BOLD)
                                tvOrderStatus.text = orderDetail.orderStatus
                                tvShippingCode.text = orderDetail.ghnOrderId
                                tvOrderDate.text = orderDetail.ngayDat
                                tvExpectedDate.text = orderDetail.ngayDuKienToi
                                tvUsername.text = orderDetail.userName
                                tvPhone.text = orderDetail.userPhone
                                tvAddress.text = orderDetail.address
                                Glide.with(requireContext())
                                    .load(orderDetail.shopAvt)
                                    .error(R.drawable.icon_avatar)
                                    .into(imgAvtShop)
                                tvShopName.text = orderDetail.shopName
                                tvShopName.setTypeface(null, Typeface.BOLD)
                                orderItemAdapter.setOrderItems(orderDetail.orderItems)
                            }
                        }
                    }
                }
            }
        }

        binding.apply {

            recyclerViewOrderItem.apply {
                adapter = orderItemAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(MarginItemDecoration(10))
            }

            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}