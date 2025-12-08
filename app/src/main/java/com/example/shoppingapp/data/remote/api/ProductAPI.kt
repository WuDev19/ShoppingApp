package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.ListCategoryAndProductDTO
import com.example.shoppingapp.data.remote.dto.get.ListProductDTO
import com.example.shoppingapp.data.remote.dto.get.ProductChiTietDTO
import com.example.shoppingapp.data.remote.dto.get.ProductDetailDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductAPI {

    @GET("sanpham/get")
    suspend fun getProducts(@Query("pageNumber") pageNumber: Int) : Response<ListProductDTO>

    @GET("sanpham/get/{theLoai}")
    suspend fun getProductByCategory(@Path("theLoai") theLoai: String, @Query("pageNumber") pageNumber: Int) : Response<ListProductDTO>

    @GET("sanpham/search/{text}")
    suspend fun searchProduct(@Path("text") text: String, @Query("pageNumber") pageNumber: Int) : Response<ListProductDTO>

    @GET("sanpham/detail/{id}")
    suspend fun getProductDetail(@Path("id") id: Int) : Response<ProductDetailDTO>

    @GET("sanpham/get/shop/{id}")
    suspend fun getOtherProductsOfShop(@Path("id") id: Int, @Query("pageNumber") pageNumber: Int) : Response<ListProductDTO>

    @GET("sanpham/get/related/{id}")
    suspend fun getSanPhamRelated(@Path("id") id: Int, @Query("pageNumber") pageNumber: Int) : Response<ListProductDTO>

    @GET("sanpham/get/spct/{id}")
    suspend fun ganSanPhamChiTiet(@Path("id") id: Int) : Response<List<ProductChiTietDTO>>

    @GET("sanpham/searchShop/{id}")
    suspend fun timKiemSanPhamCuaShop(@Path("id") id: Int, @Query("text") text: String, @Query("pageNumber") pageNumber: Int): Response<ListProductDTO>

    @GET("sanpham/bestSeller/{id}")
    suspend fun getBestSellerProductShop(@Path("id") id: Int, @Query("pageNumber") pageNumber: Int) : Response<ListProductDTO>

    @GET("sanpham/categoryProduct/{id}")
    suspend fun getCategoryAndProductByShop(@Path("id") id: Int, @Query("tenDanhMuc") tenDanhMuc: String, @Query("pageNumber") pageNumber: Int) : Response<ListCategoryAndProductDTO>

}