package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.shoppingapp.databinding.FragmentDashBoardSellerBinding
import com.example.shoppingapp.ui.viewmodel.seller.DashBoardSellerViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.ViewPagerUtils
import com.example.shoppingapp.ui.view.adapter.DashBoardSellerAdapter
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
    private lateinit var dashBoardSellerAdapter: DashBoardSellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBoardSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashBoardSellerAdapter = DashBoardSellerAdapter()

        binding.apply {

            recyclerViewDashBoard.apply {
                adapter = dashBoardSellerAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            btnNewProduct.setOnClickListener {
                controller.navigate(R.id.action_dashBoardSellerFragment_to_postProductFragment)
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
                ViewPagerUtils.enableViewPager(viewPager2, controller)
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
            }

            imgSetting.setOnClickListener {
                controller.navigate(R.id.action_dashBoardSellerFragment_to_sellerSettingFragment)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}