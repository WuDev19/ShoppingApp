package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.FragmentSellerBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ViewPagerShopAdapter
import com.example.shoppingapp.ui.viewmodel.seller.SellerViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment() {

    private val viewModel: SellerViewModel by viewModels()
    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!
    private val shopViewPagerAdapter by lazy {
        ViewPagerShopAdapter(requireActivity())
    }
    private val idSeller by lazy {
        arguments?.getInt(StringConstants.ID_SELLER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idSeller?.let {
            viewModel.getSellerDetail(it)
        } ?: ToastUtils.showToast(requireContext(), "Something went wrong")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager2.apply {
            adapter = shopViewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when(position){
                0 -> tab.text = getString(R.string.products)
                1 -> tab.text = getString(R.string.category)
                2 -> tab.text = getString(R.string.best_sellers)
            }
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.sellerDetailState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> ToastUtils.showToast(requireContext(), it.e.toString())
                        is StateListener.Success -> {
                            val seller = it.result
                            binding.tvShopName.text = seller.tenShop
                            binding.tvTotalProduct.text = seller.quantityOnSale.toString()
                            Glide.with(binding.root)
                                .load(seller.avtShop)
                                .placeholder(R.drawable.place_holder)
                                .error(R.drawable.icon_avatar)
                                .into(binding.imgAvatar)
                        }
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}