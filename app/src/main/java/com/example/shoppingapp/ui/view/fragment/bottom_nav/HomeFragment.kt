package com.example.shoppingapp.ui.view.fragment.bottom_nav

import android.annotation.SuppressLint
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.utils.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentHomeBinding
import com.example.shoppingapp.domain.model.PopularCategories
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.PopularCategoriesAdapter
import com.example.shoppingapp.ui.view.adapter.ProductAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.ShareViewModel
import com.example.shoppingapp.ui.viewmodel.bottom_nav.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.LocalTime

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val viewModelShare: ShareViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: NavController
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(R.id.bottom_navigation_view)
    }
    private val viewPager2: ViewPager2 by lazy {
        requireActivity().findViewById(R.id.view_pager2)
    }
    private val categoriesAdapter: PopularCategoriesAdapter by lazy {
        PopularCategoriesAdapter(listPopularCategories) {
            lifecycleScope.launch {
                viewModelShare.shareName(it)
            }
            bottomNavigationView.selectedItemId = R.id.store
        }
    }
    private val listPopularCategories: List<PopularCategories> by lazy {
        listOf(
            PopularCategories(1, R.drawable.icon_sport, StringConstants.SPORT),
            PopularCategories(2, R.drawable.icon_furniture, StringConstants.FURNITURE),
            PopularCategories(2, R.drawable.icon_cpu, StringConstants.ELECTRONIC),
            PopularCategories(2, R.drawable.icon_clothes, StringConstants.CLOTH),
            PopularCategories(2, R.drawable.icon_shoes, StringConstants.SHOE),
            PopularCategories(2, R.drawable.icon_cosmstic, StringConstants.COSMETIC),
            PopularCategories(2, R.drawable.icon_mobile, StringConstants.MOBILE),
            PopularCategories(2, R.drawable.icon_laptop, StringConstants.LAPTOP),
        )
    }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter { productId, sellerId ->
            BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
            ViewPagerUtils.disableViewPager(viewPager2)
            val bundle = bundleOf(
                StringConstants.ID_PRODUCT to productId,
                StringConstants.ID_SELLER to sellerId
            )
            controller.navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
        }
    }
    private var isSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getUserName()

        lifecycleScope.launch {
            viewModel.searchProduct.collectLatest {
                if (isSearch) {
                    productAdapter.submitData(it)
                } else {
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
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    // TODO: refractor lai logic
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNumberCartItem()

        controller = NavHostFragment.findNavController(this@HomeFragment)

        binding.apply {

            recyclerViewProducts.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = productAdapter
                addItemDecoration(MarginItemDecoration(5))
            }

            recyclerViewCategories.apply {
                layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
                adapter = categoriesAdapter
            }
            //cách fix xung đột cuộn khi dùng recycler view lồng trong
            recyclerViewCategories.addOnItemTouchListener(object : OnItemTouchListener {

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    rv.parent.requestDisallowInterceptTouchEvent(true) //chặn sroll cửa thằng view cha
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

            })

            tvViewAll.setOnClickListener {
                bottomNavigationView.selectedItemId = R.id.store
            }

            imgCart.setOnClickListener {
                ViewPagerUtils.disableViewPager(viewPager2)
                BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
                controller.navigate(R.id.action_homeFragment_to_cartFragment)
            }

            imgChat.setOnClickListener {
                ViewPagerUtils.disableViewPager(viewPager2)
                BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
                val bundle = bundleOf(
                    StringConstants.CURRENT_USER_ID to viewModel.getCurrentUserId()
                )
                controller.navigate(R.id.action_homeFragment_to_recentChatFragment, bundle)
            }

            edtSearch.addTextChangedListener {
                viewModel.setSearchProductStateFlow(it.toString())
                isSearch = it.toString().isNotEmpty()
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            val listSlideModel = mutableListOf<SlideModel>()
            listPopularCategories.asFlow().transform {
                val slideModel = SlideModel(it.intRes, ScaleTypes.FIT)
                emit(slideModel)
            }.collect {
                listSlideModel.add(it)
            }
            binding.imageSlider.setImageList(listSlideModel)

            // TODO: xu ly click 
            binding.imageSlider.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {

                }

                override fun onItemSelected(position: Int) {
                    ToastUtils.showToast(
                        requireContext(),
                        "${listSlideModel[position].imagePath} va $position"
                    )
                }

            })
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateUserName.collectLatest {
                        when (it) {
                            is StateListener.Idle -> {
                                val hour = LocalTime.now().hour
                                val time = if (hour < 12) {
                                    StringConstants.MORNING
                                } else if (hour in 12..18) {
                                    StringConstants.AFTERNOON
                                } else {
                                    StringConstants.EVENING
                                }
                                binding.tvGreeting.text = time
                            }

                            is StateListener.Success -> {
                                binding.tvUsername.text = it.result
                            }

                            is StateListener.Error -> {
                                ToastUtils.showToast(requireContext(), it.e.toString())
                            }

                            is StateListener.Loading -> {}
                        }
                    }
                }
                launch {
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}