package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutDashBoardSellerBinding
import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.domain.model.seller.SellerDashboard
import com.example.shoppingapp.domain.model.seller.SellerMonthlyStats
import com.example.shoppingapp.ui.state.DashboardError
import com.example.shoppingapp.ui.state.SellerDashboardState
import com.example.shoppingapp.utils.DashboardConstants
import com.example.shoppingapp.utils.StringConstants
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.color.MaterialColors
import java.text.NumberFormat
import java.util.Locale

class DashBoardSellerAdapter(
    private val retrySummary: () -> Unit,
    private val retryMonthly: () -> Unit
) : RecyclerView.Adapter<DashBoardSellerAdapter.DashBoardSellerViewHolder>() {
    private var state = SellerDashboardState(DashboardFilter.current())
    private val locale = Locale(DashboardConstants.LOCALE_LANGUAGE, DashboardConstants.LOCALE_COUNTRY)
    private val number = NumberFormat.getIntegerInstance(locale)
    private val money = NumberFormat.getCurrencyInstance(locale)
    fun submitState(value: SellerDashboardState) { state = value; notifyItemChanged(0) }
    override fun getItemCount() = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DashBoardSellerViewHolder(
        LayoutDashBoardSellerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    override fun onBindViewHolder(holder: DashBoardSellerViewHolder, position: Int) {
        with(holder.binding) {
            summaryProgress.isVisible = state.summary.loading
            monthlyProgress.isVisible = state.monthly.loading
            monthlyHeading.isVisible = state.signedIn
            summaryContent.isVisible = state.signedIn && state.summary.data != null
            monthlyContent.isVisible = state.signedIn && state.monthly.data != null
            val summaryFailure = if (state.signedIn) state.summary.error else DashboardError.ACCESS
            val monthlyFailure = if (state.signedIn) state.monthly.error else null
            error(summaryError, summaryFailure, state.summary.data != null)
            error(monthlyError, monthlyFailure, state.monthly.data != null)
            summaryRetry.isVisible = state.signedIn && summaryFailure != null && summaryFailure != DashboardError.ACCESS
            monthlyRetry.isVisible = state.signedIn && monthlyFailure != null && monthlyFailure != DashboardError.ACCESS
            summaryRetry.setOnClickListener { retrySummary() }
            monthlyRetry.setOnClickListener { retryMonthly() }
            state.summary.data?.let { bindSummary(this, it) }
            state.monthly.data?.let { bindMonthly(this, it) }
        }
    }
    private fun error(view: TextView, error: DashboardError?, stale: Boolean) {
        view.isVisible = error != null
        if (error == null) return
        val context = view.context
        val message = context.getString(when (error) {
            DashboardError.ACCESS -> R.string.dashboard_access
            DashboardError.NETWORK -> R.string.dashboard_network
            DashboardError.SERVER -> R.string.dashboard_server
        })
        view.text = if (stale) message + "\n" + context.getString(R.string.dashboard_stale) else message
    }
    private fun bindSummary(binding: LayoutDashBoardSellerBinding, value: SellerDashboard) = with(binding) {
        val context = root.context
        val rows = listOf(
            context.getString(R.string.dashboard_total_orders, number.format(value.totalOrders)),
            context.getString(R.string.dashboard_completed_orders, number.format(value.completedOrders)),
            context.getString(R.string.dashboard_order_value, money.format(value.completedOrderValue)),
            context.getString(R.string.dashboard_online_paid, money.format(value.onlinePaidAmount)),
            context.getString(R.string.dashboard_cod_value, money.format(value.completedCodOrderValue)),
            context.getString(R.string.dashboard_units, number.format(value.unitsSold)),
            context.getString(R.string.dashboard_products, number.format(value.totalProducts)),
            context.getString(R.string.dashboard_stock, number.format(value.totalStock)),
            context.getString(R.string.dashboard_out_of_stock, number.format(value.outOfStockVariants))
        )
        val accent = ContextCompat.getColor(context, R.color.button_color)
        val sb = android.text.SpannableStringBuilder()
        rows.forEachIndexed { index, row ->
            val start = sb.length
            sb.append(row)
            val colon = row.indexOf(':')
            if (colon != -1 && colon + 1 < row.length) {
                val valStart = start + colon + 1
                val valEnd = start + row.length
                sb.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), valStart, valEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                sb.setSpan(android.text.style.ForegroundColorSpan(accent), valStart, valEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (index < rows.size - 1) sb.append("\n")
        }
        summaryText.text = sb
        summaryEmpty.isVisible = value.totalOrders == 0L
        undatedWarning.isVisible = value.undatedCompletedOrders > 0
        undatedWarning.text = context.getString(R.string.dashboard_undated, number.format(value.undatedCompletedOrders))
        statusText.text = value.ordersByStatus.joinToString("\n") {
            context.getString(R.string.dashboard_status_row, status(context, it.status), number.format(it.orderCount))
        }
        val foreground = MaterialColors.getColor(root, com.google.android.material.R.attr.colorOnSurface)
        val primary = MaterialColors.getColor(root, androidx.appcompat.R.attr.colorPrimary)
        val secondary = MaterialColors.getColor(root, com.google.android.material.R.attr.colorTertiary)
        chartCategory.isVisible = value.ordersByStatus.any { it.orderCount > 0 }
        if (chartCategory.isVisible) {
            val entries = value.ordersByStatus.filter { it.orderCount > 0 }.map { PieEntry(it.orderCount.toFloat(), status(context, it.status)) }
            chartCategory.data = PieData(PieDataSet(entries, "").apply {
                colors = listOf(
                    ContextCompat.getColor(context, R.color.button_color),
                    ContextCompat.getColor(context, R.color.chart_green),
                    ContextCompat.getColor(context, R.color.chart_orange),
                    ContextCompat.getColor(context, R.color.chart_purple),
                    ContextCompat.getColor(context, R.color.chart_blue),
                    ContextCompat.getColor(context, R.color.red)
                )


                setDrawValues(false)
            })
            chartCategory.apply {
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = foreground
                legend.isWordWrapEnabled = true
                setDrawEntryLabels(false)
                isRotationEnabled = false
                setHoleColor(MaterialColors.getColor(root, com.google.android.material.R.attr.colorSurface))
                invalidate()
            }
        } else chartCategory.clear()
        val daily = value.dailySales.sortedBy { it.date }
        configure(chartDaily, foreground)
        val dailyLabels = daily.map { item ->
            val parts = item.date.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}" else DashboardFilter.display(item.date)
        }
        chartDaily.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(dailyLabels)
            labelRotationAngle = -35f
            setLabelCount(minOf(daily.size, 5), false)
        }
        chartDaily.extraBottomOffset = 12f
        val brandColor = ContextCompat.getColor(context, R.color.button_color)
        val greenColor = ContextCompat.getColor(context, R.color.chart_green)
        val completed = LineDataSet(daily.mapIndexed { index, day ->
            Entry(index.toFloat(), (day.completedOrderValue / DashboardConstants.MILLION).toFloat())
        }, context.getString(R.string.dashboard_value_legend)).apply { color = brandColor; lineWidth = 2.5f; mode = LineDataSet.Mode.CUBIC_BEZIER; setDrawCircles(daily.size == 1); setCircleColor(brandColor); setDrawValues(false) }
        val paid = LineDataSet(daily.mapIndexed { index, day ->
            Entry(index.toFloat(), (day.onlinePaidAmount / DashboardConstants.MILLION).toFloat())
        }, context.getString(R.string.dashboard_paid_legend)).apply { color = greenColor; lineWidth = 2.5f; mode = LineDataSet.Mode.CUBIC_BEZIER; setDrawCircles(daily.size == 1); setCircleColor(greenColor); setDrawValues(false) }
        chartDaily.data = LineData(completed, paid)
        chartDaily.invalidate()
        topEmpty.isVisible = value.topProducts.isEmpty()
        topProducts.removeAllViews()
        value.topProducts.forEachIndexed { index, product ->
            topProducts.addView(TextView(context).apply {
                text = "#${index + 1}  " + context.getString(R.string.dashboard_top_row,
                    product.description?.takeIf { it.isNotBlank() } ?: context.getString(R.string.dashboard_unnamed_product, product.productId),
                    number.format(product.unitsSold))
                setTextColor(foreground)
                setBackgroundResource(when (index) {
                    0 -> R.drawable.bg_gold_badge
                    1 -> R.drawable.bg_silver_badge
                    else -> R.drawable.bg_chip_action
                })
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.dashboard_text_size))
                val padH = resources.getDimensionPixelSize(R.dimen.dashboard_spacing)
                val padV = resources.getDimensionPixelSize(R.dimen.dashboard_small_spacing)
                setPadding(padH, padV, padH, padV)
                layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = padV / 2 }

            })
        }
    }
    private fun bindMonthly(binding: LayoutDashBoardSellerBinding, value: SellerMonthlyStats) = with(binding) {
        val context = root.context
        val months = value.months.sortedBy { it.month }
        monthlyEmpty.isVisible = months.all { it.completedOrderValue == 0L && it.onlinePaidAmount == 0L && it.unitsSold == 0L }
        monthlyUndated.isVisible = value.undatedCompletedOrders > 0
        monthlyUndated.text = context.getString(R.string.dashboard_undated, number.format(value.undatedCompletedOrders))
        monthlyText.text = months.joinToString("\n") {
            context.getString(R.string.dashboard_month_row, it.month, money.format(it.completedOrderValue),
                money.format(it.onlinePaidAmount), number.format(it.unitsSold))
        }
        configure(chartRevenue, MaterialColors.getColor(root, com.google.android.material.R.attr.colorOnSurface))
        chartRevenue.xAxis.valueFormatter = IndexAxisValueFormatter(months.map { it.month.toString() })
        // Two series are offset, never stacked: online receipts are not additional completed-order value.
        val width = DashboardConstants.CHART_BAR_WIDTH / 2
        val completed = BarDataSet(months.mapIndexed { index, month ->
            BarEntry(index.toFloat() - width / 2, (month.completedOrderValue / DashboardConstants.MILLION).toFloat())
        }, context.getString(R.string.dashboard_value_legend)).apply {
            color = ContextCompat.getColor(context, R.color.button_color); setDrawValues(false)
        }
        val paid = BarDataSet(months.mapIndexed { index, month ->
            BarEntry(index.toFloat() + width / 2, (month.onlinePaidAmount / DashboardConstants.MILLION).toFloat())
        }, context.getString(R.string.dashboard_paid_legend)).apply {
            color = ContextCompat.getColor(context, R.color.chart_green); setDrawValues(false)
        }
        chartRevenue.data = BarData(completed, paid).apply { barWidth = width }
        chartRevenue.invalidate()
    }
    private fun configure(chart: BarLineChartBase<*>, foreground: Int) = with(chart) {
        description.isEnabled = false
        axisRight.isEnabled = false
        axisLeft.axisMinimum = 0f
        axisLeft.textColor = foreground
        legend.textColor = foreground
        legend.isWordWrapEnabled = true
        legend.textSize = DashboardConstants.CHART_TEXT_SIZE
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            textColor = foreground
            textSize = DashboardConstants.CHART_TEXT_SIZE
            setLabelCount(DashboardConstants.CHART_LABEL_COUNT, false)
            setDrawGridLines(false)
        }
        setScaleEnabled(false)
        setDrawGridBackground(false)
    }
    private fun status(context: android.content.Context, value: String): String = when (value) {
        StringConstants.STATUS_PENDING_PAYMENT -> context.getString(R.string.dashboard_status_pending)
        StringConstants.STATUS_AWAIT_CONFIRMATION -> context.getString(R.string.dashboard_status_confirm)
        StringConstants.STATUS_PROCESSING -> context.getString(R.string.dashboard_status_processing)
        StringConstants.STATUS_SHIPPING -> context.getString(R.string.dashboard_status_shipping)
        StringConstants.STATUS_SHIPPED -> context.getString(R.string.dashboard_status_shipped)
        StringConstants.STATUS_COMPLETED -> context.getString(R.string.dashboard_status_completed)
        StringConstants.STATUS_CANCELLED -> context.getString(R.string.dashboard_status_cancelled)
        else -> value
    }
    class DashBoardSellerViewHolder(val binding: LayoutDashBoardSellerBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            listOf(binding.summaryHeading, binding.statusHeading, binding.dailyHeading,
                binding.topHeading, binding.monthlyHeading).forEach { ViewCompat.setAccessibilityHeading(it, true) }
        }
    }
}
