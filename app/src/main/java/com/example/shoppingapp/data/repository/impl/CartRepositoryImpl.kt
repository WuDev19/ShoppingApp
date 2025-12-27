package com.example.shoppingapp.data.repository.impl

import android.content.SharedPreferences
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.mapper.CartEntityMapper
import com.example.shoppingapp.data.remote.dto.request.CartAndCartItemRequest
import com.example.shoppingapp.data.remote.api.CartAPI
import com.example.shoppingapp.data.repository.mediator.CartItemInCartRemoteMediator
import com.example.shoppingapp.data.repository.mediator.CartUserRemoteMediator
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.repository.cart.CartRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartAPI: CartAPI,
    private val appDatabase: AppDatabase,
    private val sharedPreferences: SharedPreferences
) : CartRepository {

    override fun createCart(cartAndCartItemRequest: CartAndCartItemRequest): Flow<StateListener<Any>> {
        return flow {
            try {
                val response = cartAPI.createCart(cartAndCartItemRequest)
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        emit(StateListener.Success(it))
                    }
                } else {
                    Log.e(
                        StringConstants.DEBUG,
                        "createCart: ${response.message()} va ${response.code()}"
                    )
                    emit(StateListener.Error(Exception(response.message())))
                }
            } catch (e: Exception) {
                emit(StateListener.Error(e))
                Log.e(StringConstants.DEBUG, "createCart: ${e.message.toString()}")
            }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getCartUser(idUser: String): Flow<PagingData<Cart>> {
        val queryKey = "Cart_$idUser"
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = CartUserRemoteMediator(cartAPI, appDatabase, idUser, queryKey),
            pagingSourceFactory = {
                appDatabase.cartDao().getPagingSourceCartUser(idUser)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                CartEntityMapper.mapToCartDomain(it)
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getCartItemInCart(idCart: Int): Flow<PagingData<CartItem>> {
        val queryKey = "cart_item_$idCart"
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = StringConstants.PAGE_SIZE
            ),
            remoteMediator = CartItemInCartRemoteMediator(cartAPI, appDatabase, idCart, queryKey),
            pagingSourceFactory = {
                appDatabase.cartItemDao().pagingSourceCartItemInCartOfUser(idCart)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                CartEntityMapper.mapCartItemEntityToCartItem(it)
            }
        }
    }

    override fun getNumberOfCartItem(id: String): Flow<StateListener<Int>> = flow {
        val numberLocal = sharedPreferences.getInt(StringConstants.NUMBER_CART_ITEM, 0)
        val response = cartAPI.getNumberOfCartItem(id)
        if (response.isSuccessful) {
            val numberRemote = response.body() ?: 0
            emit(StateListener.Success(numberRemote))
            if (numberLocal != numberRemote) {
                sharedPreferences.edit().putInt(StringConstants.NUMBER_CART_ITEM, numberRemote)
                    .apply()
            }
        } else {
            emit(StateListener.Success(numberLocal))
        }
    }.catch {
        val numberLocal = sharedPreferences.getInt(StringConstants.NUMBER_CART_ITEM, 0)
        emit(StateListener.Success(numberLocal))
    }.flowOn(Dispatchers.IO)

}