package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val repo: UserRepository) {

}