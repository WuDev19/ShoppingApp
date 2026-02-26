package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAddressUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(userId: String): Flow<List<AddressItem>> = repo.getUserAddress(userId)

}