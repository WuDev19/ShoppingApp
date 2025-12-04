package com.example.shoppingapp.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shoppingapp.data.remote.mapper.ProductDTOMapper
import com.example.shoppingapp.data.remote.api.ProductAPI
import com.example.shoppingapp.domain.model.Product

class PagingSourceOtherProductOfShop(private val productAPI: ProductAPI, private val id: Int) :
    PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
                ?: state.closestPageToPosition(it)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val pageNumber = params.key ?: 0
            val response = productAPI.getOtherProductsOfShop(id, pageNumber)
            if(response.isSuccessful){
                val data = response.body()
                val listSanPham = data?.contents?.map {
                    ProductDTOMapper.mapToProduct(it)
                } ?: emptyList()
                LoadResult.Page(
                    listSanPham,
                    prevKey = if(pageNumber == 0) null else pageNumber - 1,
                    nextKey = if(listSanPham.isEmpty()) null else pageNumber + 1
                )
            }
            else{
                LoadResult.Error(Exception("API Error"))
            }
        }
        catch (e: Exception){
            LoadResult.Error(e)
        }
    }

}