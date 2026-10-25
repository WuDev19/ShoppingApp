package com.example.shoppingapp.domain.model.seller

import com.example.shoppingapp.utils.DashboardConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class DashboardFilter(val fromDate: String, val toDate: String, val year: Int) {
    fun isValid(): Boolean {
        if (year !in DashboardConstants.MIN_YEAR..DashboardConstants.MAX_YEAR) return false
        val start = parse(fromDate) ?: return false
        val end = parse(toDate) ?: return false
        if (start.after(end)) return false
        val limit = calendar(start).apply { add(Calendar.DAY_OF_MONTH, DashboardConstants.MAX_DAYS) }
        return end.before(limit.time)
    }

    companion object {
        private fun formatter(pattern: String) = SimpleDateFormat(pattern, Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone(DashboardConstants.TIME_ZONE)
        }
        fun parse(value: String): Date? = try {
            val format = formatter(DashboardConstants.ISO_DATE)
            format.parse(value)?.takeIf { format.format(it) == value && calendar(it).get(Calendar.YEAR) in
                DashboardConstants.MIN_YEAR..DashboardConstants.MAX_YEAR }
        } catch (_: java.text.ParseException) { null }
        fun iso(date: Date): String = formatter(DashboardConstants.ISO_DATE).format(date)
        fun display(value: String): String = parse(value)?.let {
            formatter(DashboardConstants.DISPLAY_DATE).format(it)
        }.orEmpty()
        fun calendar(date: Date = Date()): Calendar = Calendar.getInstance(
            TimeZone.getTimeZone(DashboardConstants.TIME_ZONE), Locale.US
        ).apply { time = date }
        fun current(): DashboardFilter {
            val now = calendar()
            val end = iso(now.time)
            now.set(Calendar.DAY_OF_MONTH, 1)
            return DashboardFilter(iso(now.time), end, now.get(Calendar.YEAR))
        }
    }
}
