package com.example.shoppingapp.domain.repository.product

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import com.example.shoppingapp.domain.model.product.ProductDetail
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProducts(): Flow<PagingData<ProductDisplay>>
    fun getProductByCategory(theLoai: String): Flow<PagingData<ProductDisplay>>
    fun searchProduct(text: String): Flow<PagingData<ProductDisplay>>
    fun getProductDetail(id: Int): Flow<StateListener<ProductDetail>>
    fun getOtherProductsOfShop(id: Int): Flow<PagingData<ProductDisplay>>
    fun getSanPhamRelated(id: Int): Flow<PagingData<ProductDisplay>>
    fun getSanPhamChiTiet(id: Int): Flow<StateListener<List<ProductChiTiet>>>
    fun timKiemSanPhamCuaShop(id: Int, text: String): Flow<PagingData<ProductDisplay>>
    fun getAllProductOfShop(id: Int): Flow<PagingData<ProductDisplay>>
    fun getBestSellerProductByShop(id: Int): Flow<PagingData<ProductDisplay>>
    fun getCategoryAndProductByShop(id: Int, tenDanhMuc: String): Flow<PagingData<ProductDisplay>>

}