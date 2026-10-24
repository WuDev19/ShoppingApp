package com.example.shoppingapp

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.core.os.bundleOf
import com.example.shoppingapp.ui.view.fragment.user.WriteReviewFragment
import com.example.shoppingapp.ui.view.fragment.user.MyReviewFragment
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.AndroidEntryPoint

/** Debug-only host for device regression tests. Not exported and excluded from release. */
@AndroidEntryPoint
class ReviewTestHostActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ReviewTestParentFragment(), "review-test-parent").commitNow()
        }
    }
}
class ReviewTestParentFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = FrameLayout(requireContext())
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return
        val intent = requireActivity().intent
        if (intent.getIntExtra("reviewId", 0) > 0) {
            MyReviewFragment().apply { arguments = bundleOf(StringConstants.REVIEW_ID to intent.getIntExtra("reviewId", 0)) }
                .show(childFragmentManager, "edit")
        } else {
            WriteReviewFragment().apply {
                arguments = bundleOf(StringConstants.ID_PRODUCT to intent.getIntExtra("productId", 1),
                    StringConstants.ORDER_ITEM_ID to intent.getIntExtra("orderItemId", 1))
            }.show(childFragmentManager, "write")
        }
    }
}
