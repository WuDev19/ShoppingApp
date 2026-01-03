package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.shoppingapp.databinding.FragmentVerifyOtpBinding
import com.example.shoppingapp.ui.viewmodel.seller.VerifyOtpViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VerifyOtpFragment(private val onDes: () -> Unit) : BottomSheetDialogFragment() {

    private val viewModel: VerifyOtpViewModel by viewModels()
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            edt1.requestFocus()
            edt1.addTextChangedListener { editable ->
                if(editable.toString().length == 1){
                    edt2.isEnabled = true
                    edt2.requestFocus()
                }
            }
            edt2.addTextChangedListener {editable ->
                if(editable.toString().length == 1){
                    edt3.isEnabled = true
                    edt3.requestFocus()
                }
                else if(editable.toString().isEmpty()){
                    edt1.requestFocus()
                }
            }
            edt3.addTextChangedListener {editable ->
                if(editable.toString().length == 1){
                    edt4.isEnabled = true
                    edt4.requestFocus()
                }
                else if(editable.toString().isEmpty()){
                    edt2.requestFocus()
                }
            }
            edt4.addTextChangedListener {editable ->
                if(editable.toString().length == 1){
                    edt5.isEnabled = true
                    edt5.requestFocus()
                }
                else if(editable.toString().isEmpty()){
                    edt3.requestFocus()
                }
            }
            edt5.addTextChangedListener {editable ->
                if(editable.toString().length == 1){
                    edt6.isEnabled = true
                    edt6.requestFocus()
                }
                else if(editable.toString().isEmpty()){
                    edt4.requestFocus()
                }
            }
            edt6.addTextChangedListener {editable ->
                if(editable.toString().length == 1){
                    isCancelable = true
                }
                else if(editable.toString().isEmpty()){
                    edt5.requestFocus()
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onDes()
    }

}