package com.example.shoppingapp.domain.usecase.seller_usecase

import com.example.shoppingapp.domain.model.ShopVouchers
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class GetShopVouchersUseCase @Inject constructor(
    private val repository: SellerRepository,
) {

    suspend operator fun invoke(sellerId: Int): StateListener<List<ShopVouchers>> =
        repository.getShopVouchers(sellerId)

}