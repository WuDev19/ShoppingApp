package com.example.shoppingapp.ui.view.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class MarginItemDecoration(private val spaceSize: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = spaceSize
            left = spaceSize
            right = spaceSize
            bottom = spaceSize
        }
    }

}