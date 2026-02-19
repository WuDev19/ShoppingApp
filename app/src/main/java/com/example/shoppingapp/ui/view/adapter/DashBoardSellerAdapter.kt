package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutDashBoardSellerBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class DashBoardSellerAdapter :
    RecyclerView.Adapter<DashBoardSellerAdapter.DashBoardSellerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashBoardSellerViewHolder {
        val binding = LayoutDashBoardSellerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashBoardSellerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DashBoardSellerViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            showCategorySoldChart(this)
            showRevenueEachMonth(this)
        }
    }

    override fun getItemCount(): Int = 1

    private fun showCategorySoldChart(binding: LayoutDashBoardSellerBinding) {
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

    private fun showRevenueEachMonth(binding: LayoutDashBoardSellerBinding) {
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

    class DashBoardSellerViewHolder(val binding: LayoutDashBoardSellerBinding) :
        RecyclerView.ViewHolder(binding.root)

}