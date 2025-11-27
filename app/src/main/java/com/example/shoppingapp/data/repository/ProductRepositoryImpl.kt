package com.example.shoppingapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shoppingapp.data.mapper.ProductMapper
import com.example.shoppingapp.data.remote.api.ProductAPI
import com.example.shoppingapp.data.remote.paging.PagingSourceOtherProductOfShop
import com.example.shoppingapp.data.remote.paging.PagingSourceProduct
import com.example.shoppingapp.data.remote.paging.PagingSourceProductByCategory
import com.example.shoppingapp.data.remote.paging.PagingSourceRelatedProduct
import com.example.shoppingapp.data.remote.paging.PagingSourceSearchProduct
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.model.ProductChiTiet
import com.example.shoppingapp.domain.model.ProductDetail
import com.example.shoppingapp.domain.repository.product.ProductRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val productAPI: ProductAPI) :
    ProductRepository {

    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 6, enablePlaceholders = false),
            pagingSourceFactory = { PagingSourceProduct(productAPI) }
        ).flow
    }

    override fun getProductByCategory(theLoai: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6, enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceProductByCategory(productAPI, theLoai)
            }
        ).flow
    }

    override fun searchProduct(text: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceSearchProduct(productAPI, text)
            }
        ).flow
    }

    override fun getProductDetail(id: Int): Flow<StateListener<ProductDetail>> = flow {
        val response = productAPI.getProductDetail(id)
        if (response.isSuccessful) {
            val dataDTO = response.body()
            dataDTO?.let {
                val data = ProductMapper.mapToProductDetail(it)
                emit(StateListener.Success(data))
            } ?: emit(StateListener.Error(Exception("Cast Error")))
        } else {
            emit(StateListener.Error(Exception("Can't not find product")))
        }
    }.flowOn(Dispatchers.IO)

    override fun getOtherProductsOfShop(id: Int): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceOtherProductOfShop(productAPI, id)
            }
        ).flow
    }

    override fun getSanPhamRelated(id: Int): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceRelatedProduct(productAPI, id)
            }
        ).flow
    }

    override fun getSanPhamChiTiet(id: Int): Flow<StateListener<List<ProductChiTiet>>> = flow {
        val response = productAPI.ganSanPhamChiTiet(id)
        if(response.isSuccessful){
            val data = response.body()
            val listProductChiTiet = data?.map {
                ProductMapper.mapToProductChiTiet(it)
            } ?: emptyList()
            emit(StateListener.Success(listProductChiTiet))
        }
        else{
            emit(StateListener.Error(Exception("Api Error")))
        }
    }.flowOn(Dispatchers.IO)

}