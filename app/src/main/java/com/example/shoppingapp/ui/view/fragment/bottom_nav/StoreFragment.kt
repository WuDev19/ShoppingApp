package com.example.shoppingapp.ui.view.fragment.bottom_nav

import android.content.res.ColorStateList
import android.graphics.Typeface
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentStoreBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.view.adapter.CategoryStoreAdapter
import com.example.shoppingapp.ui.view.adapter.SellerInStoreAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.ShareViewModel
import com.example.shoppingapp.ui.viewmodel.bottom_nav.StoreViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StoreFragment : Fragment() {

    private val viewModel: StoreViewModel by viewModels()
    private val viewModelShare: ShareViewModel by activityViewModels()
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
    private val listName = listOf(
        StringConstants.ALL,
        StringConstants.SPORT,
        StringConstants.FURNITURE,
        StringConstants.ELECTRONIC,
        StringConstants.CLOTH,
        StringConstants.SHOE,
        StringConstants.COSMETIC,
        StringConstants.MOBILE,
        StringConstants.LAPTOP
    )
    private val pairPositionAndName = mapOf(
        StringConstants.ALL to 0,
        StringConstants.SPORT to 1,
        StringConstants.FURNITURE to 2,
        StringConstants.ELECTRONIC to 3,
        StringConstants.CLOTH to 4,
        StringConstants.SHOE to 5,
        StringConstants.COSMETIC to 6,
        StringConstants.MOBILE to 7,
        StringConstants.LAPTOP to 8
    )
    private lateinit var name: String
    private val bottomView by lazy {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
    }
    private val viewPager2: ViewPager2 by lazy {
        requireActivity().findViewById(R.id.view_pager2)
    }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(onItemClick = {
            productId, sellerId ->
            BottomNavigationUtils.hideBottomNavigation(bottomView)
            ViewPagerUtils.disableViewPager(viewPager2)
            categoryStoreAdapter.setListSaveTrace()
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            controller.navigate(R.id.action_storeFragment_to_detailProductFragment2, bundle)
        })
    }
    private val categoryStoreAdapter: CategoryStoreAdapter by lazy {
        CategoryStoreAdapter(listName) {
            lifecycleScope.launch {
                if (it == StringConstants.ALL) {
                    viewModel.getProduct().collectLatest {
                        productAdapter.submitData(it)
                    }
                } else {
                    viewModel.getProductByCategory(it).collectLatest {
                        productAdapter.submitData(it)
                    }
                }
            }
        }
    }
    private val sellerInStoreAdapter: SellerInStoreAdapter by lazy {
        SellerInStoreAdapter {
            BottomNavigationUtils.hideBottomNavigation(bottomView)
            ViewPagerUtils.disableViewPager(viewPager2)
            categoryStoreAdapter.setListSaveTrace()
            val bundle = bundleOf(
                StringConstants.ID_SELLER to it
            )
            controller.navigate(R.id.action_storeFragment_to_sellerFragment2, bundle)
        }
    }
    private lateinit var controller: NavController
    private var shouldScroll = false
    private var isSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //click tu home fragment sang
        lifecycleScope.launch {
            viewModelShare.state.collectLatest { it ->
                if (it != null) {
                    shouldScroll = true
                    name = it
                    viewModel.getProductByCategory(it).collectLatest {
                        productAdapter.submitData(it)
                    }
                } else {
                    shouldScroll = false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getAllSeller().collectLatest {
                sellerInStoreAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            viewModel.searchFlow.collectLatest {
                if (isSearch) {
                    setHighlightCategory(0)
                    productAdapter.submitData(it)
                } else {
                    //mac dinh ban dau se hien full
                    setHighlightCategory(0)
                    viewModel.getProduct().collectLatest { it1 ->
                        productAdapter.submitData(it1)
                    }
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNumberCartItem()

        controller = NavHostFragment.findNavController(this@StoreFragment)

        binding.apply {

            recyclerViewProductStore.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = productAdapter
                addItemDecoration(MarginItemDecoration(5))
            }

            recyclerViewCategories.apply {
                layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
                adapter = categoryStoreAdapter
            }

            recyclerViewShop.apply {
                layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
                adapter = sellerInStoreAdapter
                addItemDecoration(MarginItemDecoration(10))
            }

            recyclerViewCategories.addOnItemTouchListener(object : OnItemTouchListener {
                //recycler_view con lồng trong thì dùng như này để fix tranh chấp cuộn với cha (như viewpager, recycler view cha)
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    rv.parent.requestDisallowInterceptTouchEvent(true) //true thì chặn thằng cha
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

            })

            recyclerViewShop.addOnItemTouchListener(object : OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    rv.parent.requestDisallowInterceptTouchEvent(true)
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

            })

            imgCart.setOnClickListener {
                ViewPagerUtils.disableViewPager(viewPager2)
                BottomNavigationUtils.hideBottomNavigation(bottomView)
                controller.navigate(R.id.action_storeFragment_to_cartFragment22)
                categoryStoreAdapter.setListSaveTrace()
            }

            edtSearch.addTextChangedListener {
                viewModel.setSearchStateFlow(it.toString())
                isSearch = it.toString().isNotEmpty()
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.numberCartState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> Log.e(
                            StringConstants.DEBUG,
                            "onViewCreated: ${it.e.toString()}"
                        )

                        is StateListener.Success -> {
                            val number = it.result
                            if (number != 0) {
                                binding.tvNumberCart.isVisible = true
                                binding.tvNumberCart.text = number.toString()
                            } else {
                                binding.tvNumberCart.isVisible = false
                            }
                        }
                    }
                }
            }
        }

        productAdapter.addLoadStateListener { state ->
            val done = state.refresh is LoadState.NotLoading
            if (done && shouldScroll) { //case click tu home sang
                shouldScroll = false
                val position = pairPositionAndName[name]
                setHighlightCategory(position)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setHighlightCategory(position: Int?){
        binding.recyclerViewCategories.smoothScrollToPosition(position ?: 0)
        binding.recyclerViewCategories.post {
            val viewHolder: CategoryStoreAdapter.CategoryStoreViewHolder? =
                binding.recyclerViewCategories.findViewHolderForAdapterPosition(
                    position ?: 0
                ) as? CategoryStoreAdapter.CategoryStoreViewHolder
            viewHolder?.let {
                it.binding.tvCategoryItem.setTypeface(null, Typeface.BOLD)
                it.binding.tvCategoryItem.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            viewHolder.itemView.context,
                            R.color.button_color
                        )
                    )
                )
                it.binding.viewCategoryItem.setBackgroundResource(R.color.button_color)
                it.binding.viewCategoryItem.scaleX = 5f
                it.binding.viewCategoryItem.scaleY = 5f
                categoryStoreAdapter.setChooseTab(position ?: 0)
            }
        }
    }

}
