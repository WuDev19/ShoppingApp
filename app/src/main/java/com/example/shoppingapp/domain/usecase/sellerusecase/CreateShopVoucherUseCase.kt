package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.data.remote.dto.request.VoucherRequest
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.TokenManager
import javax.inject.Inject

class CreateShopVoucherUseCase @Inject constructor(
    private val repo: SellerRepository,
    private val tokenManager: TokenManager
) {

    suspend operator fun invoke(voucherRequest: VoucherRequest): StateListener<Unit> =
        repo.createVoucher(tokenManager.getSellerId(), voucherRequest)

}