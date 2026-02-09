package com.example.shoppingapp.ui.view.fragment.seller

import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.shoppingapp.databinding.FragmentDashBoardSellerBinding
import com.example.shoppingapp.ui.viewmodel.seller.DashBoardSellerViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import androidx.core.graphics.toColorInt
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.ViewPagerUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashBoardSellerFragment : Fragment() {

    private val viewModel: DashBoardSellerViewModel by viewModels()
    private val args: DashBoardSellerFragmentArgs by navArgs()
    private var _binding: FragmentDashBoardSellerBinding? = null
    private val binding
        get() = _binding!!

    private val userId: String
        get() = args.userId

    private val controller by lazy {
        findNavController()
    }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(R.id.bottom_navigation_view)
    }
    private val viewPager2: ViewPager2 by lazy {
        requireActivity().findViewById(R.id.view_pager2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBoardSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showCategorySoldChart(binding)
        showRevenueEachMonth(binding)
        binding.apply {
            btnNewProduct.setOnClickListener {
                controller.navigate(R.id.action_dashBoardSellerFragment_to_postProductFragment)
            }
            imgBack.setOnClickListener {
                controller.popBackStack()
                ViewPagerUtils.enableViewPager(viewPager2, controller)
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCategorySoldChart(binding: FragmentDashBoardSellerBinding) {
        binding.apply {
            val entry1 = PieEntry(30f, "Sport")
            val entry2 = PieEntry(20f, "Cosmetic")
            val entry3 = PieEntry(50f, "Shoes")
            val pie = PieDataSet(listOf(entry1, entry2, entry3), "Thong ke")
            pie.apply {
                colors = listOf(
                    "#4CAF50".toColorInt(),
                    "#2196F3".toColorInt(),
                    "#FF9800".toColorInt(),
                )
                sliceSpace = 3f
                selectionShift = 9f
            }
            val dataToChart = PieData(pie)
            dataToChart.apply {
                setDrawValues(true)
                isHighlightEnabled = true
                setValueFormatter(PercentFormatter(chartCategory))
            }
            chartCategory.apply {
                data = dataToChart
                description.isEnabled = false
                setUsePercentValues(true)
                isRotationEnabled = false
                description.isEnabled = false
                invalidate()
            }

        }
    }

    private fun showRevenueEachMonth(binding: FragmentDashBoardSellerBinding) {
        binding.apply {
            val entry1 = BarEntry(1f, 50f)
            val entry2 = BarEntry(2f, 60f)
            val entry3 = BarEntry(3f, 70f)
            val entry4 = BarEntry(4f, 80f)
            val entry5 = BarEntry(5f, 90f)
            val entry6 = BarEntry(6f, 100f)
            val entry7 = BarEntry(7f, 110f)
            val entry8 = BarEntry(8f, 120f)
            val entry9 = BarEntry(9f, 130f)
            val entry10 = BarEntry(10f, 140f)
            val entry11 = BarEntry(11f, 150f)
            val entry12 = BarEntry(12f, 160f)
            val listEntry = listOf(
                entry1,
                entry2,
                entry3,
                entry4,
                entry5,
                entry6,
                entry7,
                entry8,
                entry9,
                entry10,
                entry11,
                entry12
            )
            val dataSet = BarDataSet(listEntry, "Revenue")
            val dataToChart = BarData(dataSet)
            chartRevenue.apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    axisMinimum = 0f
                    mAxisRange = 1f
                    granularity = 1f //tránh duplicate khi zoom
                }
                data = dataToChart
                invalidate()
            }

        }
    }

}