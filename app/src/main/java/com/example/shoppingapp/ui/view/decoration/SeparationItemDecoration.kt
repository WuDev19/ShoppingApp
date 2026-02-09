package com.example.shoppingapp.ui.view.decoration

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SeparationItemDecoration(
    private val height: Int,
    private val mauSac: Int
) : ItemDecoration() {
    //định nghĩa bút vẽ
    private val paint = Paint().apply {
        this.color = mauSac //màu sắc của bút
        this.strokeWidth = height.toFloat() //độ rộng nét bút
        this.style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width - parent.paddingRight).toFloat()

        val childCount = parent.childCount

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top + height
            //canvas như cái giấy để paint (bút) vẽ lên
            c.drawRect(left, top, right, bottom, paint)
        }
    }
}