package com.example.shoppingapp.ui.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shoppingapp.ui.view.fragment.seller.BestSellerFragment
import com.example.shoppingapp.ui.view.fragment.seller.CategoryShopFragment
import com.example.shoppingapp.ui.view.fragment.seller.ProductShopFragment

class ViewPagerShopAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProductShopFragment()
            1 -> CategoryShopFragment()
            else -> BestSellerFragment()
        }
    }

}