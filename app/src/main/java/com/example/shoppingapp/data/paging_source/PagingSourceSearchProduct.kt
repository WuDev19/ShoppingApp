package com.example.shoppingapp.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shoppingapp.data.remote.mapper.ProductDTOMapper
import com.example.shoppingapp.data.remote.api.ProductAPI
import com.example.shoppingapp.domain.model.Product

class PagingSourceSearchProduct(private val productAPI: ProductAPI, private val text: String) :
    PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey
                ?: state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val offset = params.key ?: 0
            val response = productAPI.searchProduct(text, offset)
            if (response.isSuccessful) {
                val data = response.body()
                val productList = data?.contents?.map {
                    ProductDTOMapper.mapToProduct(it)
                } ?: emptyList()
                LoadResult.Page(
                    productList,
                    prevKey = if (offset == 0) null else offset - 1,
                    nextKey = if (productList.isEmpty()) null else offset + 1
                )
            } else {
                LoadResult.Error(Exception("API ERROR"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}