package com.example.shoppingapp.ui.view.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutSellerSettingBinding
import com.example.shoppingapp.domain.model.SellerSetting

class SettingSellerAdapter(
    private val onChangeName: (String) -> Unit,
    private val onChangeAddress: (String) -> Unit,
    private val onChangePhone: (String) -> Unit,
    private val onChangeAvatar: () -> Unit,
    private val onShowGallery: () -> Unit,
    private val onShowPreviewAvatar: ((String) -> Unit)? = null
) : RecyclerView.Adapter<SettingSellerAdapter.SettingSellerViewHolder>() {

    private var isClickChangePhone = false
    private var isClickChangeName = false
    private var isClickChangeAddress = false
    private var isRefresh = false
    private var uri: Uri? = null
    private var url: String? = null
    private var seller: SellerSetting? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setAvatar(uri: Uri? = null, seller: SellerSetting? = null) {
        this.uri = uri
        this.url = seller?.avtShop
        this.seller = seller
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(sellerSetting: SellerSetting) {
        seller = sellerSetting
        isRefresh = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingSellerViewHolder {
        val binding =
            LayoutSellerSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingSellerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SettingSellerViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            if (isRefresh) {
                isRefresh = false
                edtAddress.setText("")
                edtPhone.setText("")
                edtName.setText("")
            }
            handleChangePhone(this)
            handleChangeName(this)
            handleChangeAddress(this)
            loadAvatar(holder)
            loadInfo(holder)
            imgGallery.setOnClickListener {
                onShowGallery()
            }
            imgAvatar.setOnClickListener {
                onShowPreviewAvatar?.invoke(url ?: "")
            }
            btnChange.setOnClickListener {
                onChangeAvatar()
            }
            btnSaveName.setOnClickListener {
                onChangeName(edtName.text.toString())
            }
            btnSavePhone.setOnClickListener {
                onChangePhone(edtPhone.text.toString())
            }
            btnSaveAddress.setOnClickListener {
                onChangeAddress(edtAddress.text.toString())
            }
        }
    }

    override fun getItemCount(): Int = 1

    class SettingSellerViewHolder(val binding: LayoutSellerSettingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun handleChangePhone(binding: LayoutSellerSettingBinding) {
        binding.apply {
            linearPhone.setOnClickListener {
                if (!isClickChangePhone) {
                    isClickChangePhone = true
                    linearChangePhone.isVisible = true
                    linearChangePhone.alpha = 0f
                    linearChangePhone.animate()
                        .setDuration(500)
                        .alpha(1f)
                        .start()
                    imgNextPhone.animate()
                        .rotation(90f)
                        .setDuration(500)
                        .start()
                } else {
                    isClickChangePhone = false
                    linearChangePhone.animate()
                        .setDuration(500)
                        .alpha(0f)
                        .withEndAction {
                            linearChangePhone.isVisible = false
                        }
                        .start()
                    imgNextPhone.animate()
                        .rotation(0f)
                        .setDuration(500)
                        .start()
                }
            }
        }
    }

    private fun handleChangeAddress(binding: LayoutSellerSettingBinding) {
        binding.apply {
            linearAddress.setOnClickListener {
                if (!isClickChangeAddress) {
                    isClickChangeAddress = true
                    linearChangeAddress.isVisible = true
                    linearChangeAddress.alpha = 0f
                    linearChangeAddress.animate()
                        .setDuration(500)
                        .alpha(1f)
                        .start()
                    imgNextAddress.animate()
                        .rotation(90f)
                        .setDuration(500)
                        .start()
                } else {
                    isClickChangeAddress = false
                    linearChangeAddress.animate()
                        .setDuration(500)
                        .alpha(0f)
                        .withEndAction {
                            linearChangeAddress.isVisible = false
                        }
                        .start()
                    imgNextAddress.animate()
                        .rotation(0f)
                        .setDuration(500)
                        .start()
                }
            }
        }
    }

    private fun handleChangeName(binding: LayoutSellerSettingBinding) {
        binding.apply {
            linearName.setOnClickListener {
                if (!isClickChangeName) {
                    isClickChangeName = true
                    linearChangeName.isVisible = true
                    linearChangeName.alpha = 0f
                    linearChangeName.animate()
                        .setDuration(500)
                        .alpha(1f)
                        .start()
                    imgNextName.animate()
                        .rotation(90f)
                        .setDuration(500)
                        .start()
                } else {
                    isClickChangeName = false
                    linearChangeName.animate()
                        .setDuration(500)
                        .alpha(0f)
                        .withEndAction {
                            linearChangeName.isVisible = false
                        }
                        .start()
                    imgNextName.animate()
                        .rotation(0f)
                        .setDuration(500)
                        .start()
                }
            }
        }
    }

    private fun loadAvatar(holder: SettingSellerViewHolder) {
        holder.binding.apply {
            when {
                uri != null -> {
                    Glide.with(this.root)
                        .load(uri)
                        .error(R.drawable.icon_avatar)
                        .into(imgAvatar)
                }

                url != null -> {
                    Glide.with(this.root)
                        .load(url)
                        .error(R.drawable.icon_seller)
                        .into(imgAvatar)
                }
            }
        }
    }

    private fun loadInfo(holder: SettingSellerViewHolder) {
        holder.binding.apply {
            seller?.let {
                it.tenShop?.let { name ->
                    tvShopName.text = name
                } ?: run { tvShopName.isVisible = false }
                it.phoneShop?.let { phone ->
                    tvShopPhone.text = phone
                } ?: run { tvShopPhone.isVisible = false }
                it.addressShop?.let { address ->
                    tvShopAddress.text = address
                } ?: run { tvShopAddress.isVisible = false }
            }
        }
    }

}

