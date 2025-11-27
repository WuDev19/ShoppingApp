package com.example.shoppingapp.ui.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shoppingapp.R

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                NavHostFragment.create(R.navigation.nav_home)
            }
            1 -> {
                NavHostFragment.create(R.navigation.nav_store)
            }
            else -> {
                NavHostFragment.create(R.navigation.nav_profile)
            }
        }
    }

}