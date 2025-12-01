package com.example.shoppingapp.domain.repository.product

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.model.ProductChiTiet
import com.example.shoppingapp.domain.model.ProductDetail
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProducts(): Flow<PagingData<Product>>
    fun getProductByCategory(theLoai: String): Flow<PagingData<Product>>
    fun searchProduct(text: String): Flow<PagingData<Product>>
    fun getProductDetail(id: Int): Flow<StateListener<ProductDetail>>
    fun getOtherProductsOfShop(id: Int): Flow<PagingData<Product>>
    fun getSanPhamRelated(id: Int): Flow<PagingData<Product>>
    fun getSanPhamChiTiet(id: Int): Flow<StateListener<List<ProductChiTiet>>>
    fun timKiemSanPhamCuaShop(id: Int, text: String): Flow<PagingData<Product>>
    fun getAllProductOfShop(id: Int): Flow<PagingData<Product>>
    fun getBestSellerProductByShop(id: Int): Flow<PagingData<Product>>

}