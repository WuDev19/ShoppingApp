package com.example.shoppingapp.ui.view.fragment.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.databinding.FragmentImageDescriptionProductBinding

class ImageDescriptionProductFragment : Fragment() {

    private var _binding: FragmentImageDescriptionProductBinding? = null
    private val binding get() = _binding!!
    private val listAnhMoTa by lazy {
        arguments?.getStringArrayList(StringConstants.IMAGE_DESCRIPTION_SLIDER) ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDescriptionProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            if(listAnhMoTa.isNotEmpty()){
                val listModel = listAnhMoTa.map {
                    SlideModel(it, ScaleTypes.CENTER_CROP)
                }
                imageDetail.setImageList(listModel)
                imageDetail.setSlideAnimation(AnimationTypes.ZOOM_IN)
            }

            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}