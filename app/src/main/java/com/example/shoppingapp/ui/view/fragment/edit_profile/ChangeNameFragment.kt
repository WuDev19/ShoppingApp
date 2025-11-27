package com.example.shoppingapp.ui.view.fragment.edit_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppingapp.R
import com.example.shoppingapp.ui.viewmodel.edit_profile.ChangeNameViewModel

class ChangeNameFragment : Fragment() {

    companion object {
        fun newInstance() = ChangeNameFragment()
    }

    private val viewModel: ChangeNameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_change_name, container, false)
    }
}