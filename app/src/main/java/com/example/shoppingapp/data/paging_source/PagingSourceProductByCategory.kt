package com.example.shoppingapp.data.paging_source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.mapper.ProductDTOMapper
import com.example.shoppingapp.data.remote.api.ProductAPI
import com.example.shoppingapp.domain.model.Product

class PagingSourceProductByCategory(
    private val productAPI: ProductAPI,
    private val theLoai: String
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey
                ?: state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val pageNumber = params.key ?: 0
            val response = productAPI.getProductByCategory(theLoai, pageNumber)
            Log.e(StringConstants.DEBUG, "load: ${params.loadSize}")
            if (response.isSuccessful) {
                val data = response.body()
                val listProduct = data?.contents?.map {
                    ProductDTOMapper.mapToProduct(it)
                } ?: emptyList()
                LoadResult.Page(
                    listProduct,
                    prevKey = if (pageNumber == 0) null else pageNumber - 1,
                    nextKey = if (listProduct.isEmpty()) null else pageNumber + 1
                )
            } else {
                LoadResult.Error(Exception("Api Error"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}