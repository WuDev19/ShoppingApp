package com.example.shoppingapp.ui.view.fragment.user

import android.animation.ValueAnimator
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentMyOrderBinding
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.model.OrderSearchOrNot
import com.example.shoppingapp.domain.model.StatusOrder
import com.example.shoppingapp.domain.model.user.OrderUpdateStatus
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.OrderViaStatusAdapter
import com.example.shoppingapp.ui.view.adapter.StatusOrderAdapter
import com.example.shoppingapp.ui.viewmodel.user.MyOrderViewModel
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ViewPagerUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private val controller by lazy { findNavController() }
    private val viewPager2: ViewPager2 by lazy { requireActivity().findViewById(R.id.view_pager2) }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(
            R.id.bottom_navigation_view
        )
    }
    private val density by lazy {
        requireContext().resources.displayMetrics.density
    }
    private val linearParams by lazy { binding.linearAppBar.layoutParams as LinearLayout.LayoutParams }
    private var status: String = StringConstants.STATUS_PENDING_PAYMENT
    private val statusOrderAdapter by lazy {
        StatusOrderAdapter(
            listOf(
                StatusOrder(1, StringConstants.STATUS_PENDING_PAYMENT, true),
                StatusOrder(2, StringConstants.STATUS_AWAIT_CONFIRMATION, false),
                StatusOrder(3, StringConstants.STATUS_PROCESSING, false),
                StatusOrder(4, StringConstants.STATUS_SHIPPING, false),
                StatusOrder(7, StringConstants.STATUS_SHIPPED, false),
                StatusOrder(5, StringConstants.STATUS_COMPLETED, false),
                StatusOrder(6, StringConstants.STATUS_CANCELLED, false),
            )
        ) { status, isSelected ->
            if (!isSelected) {
                Log.e(TAG, "click: ")
                this.status = status
                if (!binding.edtSearch.isVisible) {
                    viewModel.setOrderSearchOrNot(OrderSearchOrNot(status, ""))
                } else {
                    viewModel.setOrderSearchOrNot(
                        OrderSearchOrNot(status, binding.edtSearch.text.toString().trim())
                    )
                }
            }
        }
    }
    private val orderViaStatusAdapter by lazy {
        OrderViaStatusAdapter(
            onDetailOrder = { orderId ->
                hideSearch(binding)
                val bundle = bundleOf(
                    StringConstants.ORDER_ID to orderId
                )
                findNavController().navigate(
                    R.id.action_myOrderFragment_to_orderDetailFragment3,
                    bundle
                )
            },
            onCancelOrder = { orderId, orderGhnId, shopGhnId ->
                showDialog(orderId, shopGhnId, orderGhnId)
            },
            onConfirmOrder = { orderId ->
                showDialog(orderId)
            }
        )
    }
    private var appBarAnimator: ValueAnimator? = null
    private val keyboard by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setOrderSearchOrNot(OrderSearchOrNot(StringConstants.STATUS_PENDING_PAYMENT, ""))
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
                launch {
                    viewModel.orderStatusSearchOrNotState.collectLatest {
                        orderViaStatusAdapter.submitData(it)
                    }
                }
                launch {
                    viewModel.cancelOrderState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
                                binding.layoutShimmer.frameShimmer.isVisible = true
                            }

                            is StateListener.Error -> {
                                Log.e(TAG, "onViewCreated: ${it.e?.message}")
                                binding.layoutShimmer.frameShimmer.isVisible = false
                            }

                            is StateListener.Success<CancelOrder> -> {
                                Log.e(TAG, "onViewCreated: ${it.result}")
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                orderViaStatusAdapter.refresh()
                            }
                        }
                    }
                }
                launch {
                    viewModel.updateConfirmStatus.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
                                binding.layoutShimmer.frameShimmer.isVisible = true
                            }

                            is StateListener.Error -> {
                                Log.e(TAG, "onViewCreated: ${it.e?.message}")
                                binding.layoutShimmer.frameShimmer.isVisible = false
                            }

                            is StateListener.Success<OrderUpdateStatus> -> {
                                Log.e(TAG, "onViewCreated: ${it.result}")
                                binding.layoutShimmer.frameShimmer.isVisible = false
                                orderViaStatusAdapter.refresh()
                            }
                        }
                    }
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

            imgBack.setOnClickListener {
                controller.popBackStack()
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
                ViewPagerUtils.enableViewPager(viewPager2, controller)
            }

            tvMyOrder.setOnClickListener {
                handleSearchView(this)
            }

            imgSearch.setOnClickListener {
                val keyword = edtSearch.text.toString().trim()
                if (edtSearch.isVisible) {
                    viewModel.setOrderSearchOrNot(OrderSearchOrNot(status, keyword))
                } else {
                    viewModel.setOrderSearchOrNot(null)
                }
            }

            edtSearch.setOnEditorActionListener { _, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    val keyword = edtSearch.text.toString().trim()
                    if (edtSearch.isVisible) {
                        viewModel.setOrderSearchOrNot(OrderSearchOrNot(status, keyword))
                    }
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleSearchView(binding: FragmentMyOrderBinding) {
        binding.apply {
            if (tvMyOrder.isVisible) {
                if (appBarAnimator?.isRunning == true) return
                appBarAnimator?.cancel()
                tvMyOrder.isVisible = false
                edtSearch.isVisible = true
                appBarAnimator = ValueAnimator.ofInt(
                    linearAppBar.height,
                    (linearAppBar.height + 30 * density).toInt()
                ).apply {
                    duration = 500
                    addUpdateListener { valueAnimator ->
                        val heightIncrease = valueAnimator.animatedValue as Int
                        linearParams.height = heightIncrease
                        linearAppBar.requestLayout()
                    }
                    start()
                }
                edtSearch.scaleX = 0f
                edtSearch.animate().apply {
                    scaleX(1f)
                    duration = 500
                    withEndAction {
                        edtSearch.requestFocus()
                        keyboard.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)
                    }
                    start()
                }

            }
        }
    }

    private fun hideSearch(binding: FragmentMyOrderBinding) {
        binding.apply {
            if (edtSearch.isVisible) {
                edtSearch.setText("")
                if (appBarAnimator?.isRunning == true) return
                appBarAnimator?.cancel()
                appBarAnimator = ValueAnimator.ofInt(
                    linearAppBar.height,
                    (linearAppBar.height - 30 * density).toInt()
                ).apply {
                    duration = 500
                    addUpdateListener { valueAnimator ->
                        val heightIncrease = valueAnimator.animatedValue as Int
                        linearParams.height = heightIncrease
                        linearAppBar.requestLayout()
                    }
                    start()
                }
                edtSearch.animate().apply {
                    scaleX(0f)
                    duration = 500
                    withEndAction {
                        tvMyOrder.isVisible = true
                        edtSearch.isVisible = false
                        keyboard.hideSoftInputFromWindow(
                            view?.windowToken, //trong fragment thì lấy token như này
                            0
                        )
                    }
                    start()
                }
            }
        }
    }

    private fun showDialog(orderId: String, shopGhnId: Int, orderGhnId: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("ShoppingApp")
            .setMessage("Are you sure you want to cancel?")
            .setNegativeButton("No") { _, _ ->
            }
            .setPositiveButton("Sure") { _, _ ->
                viewModel.cancelOrder(orderId, shopGhnId, orderGhnId)
            }
            .create()
        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawableResource(R.drawable.custom_dialog)
        }
    }

    private fun showDialog(orderId: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("ShoppingApp")
            .setMessage("Have you received your order?")
            .setNegativeButton("No") { _, _ ->
            }
            .setPositiveButton("Confirm") { _, _ ->
                viewModel.confirmReceivedOrder(orderId, StringConstants.STATUS_COMPLETED)
            }
            .create()
        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawableResource(R.drawable.custom_dialog)
        }
    }

}