package com.example.shoppingapp.ui.view.fragment.user

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppingapp.R
import com.example.shoppingapp.ui.viewmodel.user.MyReviewViewModel

class MyReviewFragment : Fragment() {

    companion object {
        fun newInstance() = MyReviewFragment()
    }

    private val viewModel: MyReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_review, container, false)
    }
}