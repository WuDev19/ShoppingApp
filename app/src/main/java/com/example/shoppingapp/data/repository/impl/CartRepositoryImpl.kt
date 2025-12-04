package com.example.shoppingapp.data.repository.impl

import android.util.Log
import androidx.paging.PagingData
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.dto.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.api.CartAPI
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.repository.cart.CartRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(private val cartAPI: CartAPI) : CartRepository {

    override fun createCart(cartAndCartItemRequest: CartAndCartItemRequest): Flow<StateListener<Any>> {
        return flow {
            try {
                val response = cartAPI.createCart(cartAndCartItemRequest)
                if(response.isSuccessful){
                    val data = response.body()
                    data?.let {
                        emit(StateListener.Success(it))
                    }
                }
                else{
                    Log.e(StringConstants.DEBUG, "createCart: ${response.message()} va ${response.code()}")
                    emit(StateListener.Error(Exception(response.message())))
                }
            }
            catch (e: Exception){
                emit(StateListener.Error(e))
                Log.e(StringConstants.DEBUG, "createCart: ${e.message.toString()}")
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getCartUser(idUser: Int): Flow<PagingData<Cart>> {
        return flow {  }
    }

}