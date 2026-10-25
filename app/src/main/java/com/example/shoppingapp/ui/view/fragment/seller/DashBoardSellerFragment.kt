package com.example.shoppingapp.ui.view.fragment.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentDashBoardSellerBinding
import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.ui.view.adapter.DashBoardSellerAdapter
import com.example.shoppingapp.ui.viewmodel.seller.DashBoardSellerViewModel
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.DashboardConstants
import com.example.shoppingapp.utils.ViewPagerUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardSellerFragment : Fragment() {
    @Inject lateinit var auth: FirebaseAuth
    private val viewModel: DashBoardSellerViewModel by viewModels()
    private val args: DashBoardSellerFragmentArgs by navArgs()
    private var binding: FragmentDashBoardSellerBinding? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewBinding = FragmentDashBoardSellerBinding.inflate(inflater, container, false)
        binding = viewBinding
        return viewBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewBinding = requireNotNull(binding)
        val adapter = DashBoardSellerAdapter(viewModel::retrySummary, viewModel::retryMonthly)
        viewBinding.recyclerViewDashBoard.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.recyclerViewDashBoard.adapter = adapter
        ViewCompat.setOnApplyWindowInsetsListener(view) { target, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            target.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(view)
        viewBinding.imgBack.setOnClickListener { back() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { back() }
        viewBinding.btnNewProduct.setOnClickListener {
            viewModel.markRefreshOnReturn()
            findNavController().navigate(R.id.action_dashBoardSellerFragment_to_postProductFragment)
        }
        viewBinding.btnNewVoucher.setOnClickListener {
            viewModel.markRefreshOnReturn()
            findNavController().navigate(R.id.action_dashBoardSellerFragment_to_createVoucherFragment)
        }
        viewBinding.imgSetting.setOnClickListener {
            viewModel.markRefreshOnReturn()
            findNavController().navigate(R.id.action_dashBoardSellerFragment_to_sellerSettingFragment)
        }
        viewBinding.btnRefresh.setOnClickListener { viewModel.refresh() }
        viewBinding.btnRange.setOnClickListener { chooseRange() }
        viewBinding.btnYear.setOnClickListener { chooseYear() }
        (childFragmentManager.findFragmentByTag(DashboardConstants.DATE_RANGE_TAG) as? MaterialDatePicker<*>)?.let {
            @Suppress("UNCHECKED_CAST")
            connectRange(it as MaterialDatePicker<androidx.core.util.Pair<Long, Long>>)
        }
        // Check the account before collecting retained state to avoid rendering another session's data.
        updateSession(auth)
        authListener = FirebaseAuth.AuthStateListener { updateSession(it) }
            .also { auth.addAuthStateListener(it) }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    viewBinding.btnRange.text = getString(R.string.dashboard_range,
                        DashboardFilter.display(state.filter.fromDate), DashboardFilter.display(state.filter.toDate))
                    viewBinding.btnYear.text = getString(R.string.dashboard_year, state.filter.year)
                    viewBinding.filterError.isVisible = state.invalidFilter
                    viewBinding.btnRefresh.isEnabled = state.signedIn && !state.summary.loading && !state.monthly.loading
                    viewBinding.btnNewProduct.isEnabled = state.signedIn
                    viewBinding.btnNewVoucher.isEnabled = state.signedIn
                    viewBinding.imgSetting.isEnabled = state.signedIn
                    adapter.submitState(state)
                }
            }
        }
    }
    private fun updateSession(firebase: FirebaseAuth) {
        val userId = firebase.currentUser?.uid?.takeIf { args.userId.isBlank() || args.userId == it }
        viewModel.onSession(userId)
    }
    override fun onResume() {
        super.onResume()
        viewModel.refreshIfNeeded()
    }
    private fun chooseRange() {
        if (childFragmentManager.findFragmentByTag(DashboardConstants.DATE_RANGE_TAG) != null) return
        val filter = viewModel.state.value.filter
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.ThemeOverlay_ShoppingApp_Dashboard_Calendar)
            .setSelection(androidx.core.util.Pair(utcMillis(filter.fromDate), utcMillis(filter.toDate))).build()
        connectRange(picker)
        picker.show(childFragmentManager, DashboardConstants.DATE_RANGE_TAG)
    }
    private fun connectRange(picker: MaterialDatePicker<androidx.core.util.Pair<Long, Long>>) {
        picker.clearOnPositiveButtonClickListeners()
        picker.addOnPositiveButtonClickListener { range ->
            viewModel.selectRange(isoUtc(range.first), isoUtc(range.second))
        }
    }
    private fun utcMillis(iso: String): Long {
        val local = DashboardFilter.calendar(requireNotNull(DashboardFilter.parse(iso)))
        return Calendar.getInstance(TimeZone.getTimeZone(DashboardConstants.UTC)).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }
    private fun isoUtc(millis: Long): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone(DashboardConstants.UTC)).apply { timeInMillis = millis }
        return DashboardFilter.iso(DashboardFilter.calendar().apply {
            clear()
            set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
        }.time)
    }
    private fun chooseYear() {
        val dialogContext = binding?.root?.context ?: return
        val picker = NumberPicker(dialogContext).apply {
            minValue = DashboardConstants.MIN_YEAR
            maxValue = DashboardConstants.MAX_YEAR
            value = viewModel.state.value.filter.year
            wrapSelectorWheel = false
        }
        MaterialAlertDialogBuilder(dialogContext).setTitle(R.string.dashboard_choose_year)
            .setView(picker).setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ -> picker.clearFocus(); viewModel.selectYear(picker.value) }.show()
    }
    private fun back() {
        val controller = findNavController()
        controller.popBackStack()
        requireActivity().findViewById<ViewPager2>(R.id.view_pager2)?.let {
            ViewPagerUtils.enableViewPager(it, controller)
        }
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.let {
            BottomNavigationUtils.showBottomNavigation(it, controller)
        }
    }
    override fun onDestroyView() {
        authListener?.let { auth.removeAuthStateListener(it) }
        authListener = null
        binding?.recyclerViewDashBoard?.adapter = null
        binding = null
        super.onDestroyView()
    }
}
