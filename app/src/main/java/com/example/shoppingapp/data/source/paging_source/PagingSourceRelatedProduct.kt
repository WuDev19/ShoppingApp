package com.example.shoppingapp.data.source.paging_source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.mapper.ProductDTOMapper
import com.example.shoppingapp.data.remote.api.product.GetProductAPI
import com.example.shoppingapp.domain.model.product.ProductDisplay

class PagingSourceRelatedProduct(private val getProductAPI: GetProductAPI, private val id: Int) :
    PagingSource<Int, ProductDisplay>() {

    override fun getRefreshKey(state: PagingState<Int, ProductDisplay>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
                ?: state.closestPageToPosition(it)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDisplay> {
        return try {
            val pageNumber = params.key ?: 0
            val response = getProductAPI.getSanPhamRelated(id, pageNumber)
            Log.e("CHECKKKK", "load: ${params.key} va ${params.loadSize}")
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
                LoadResult.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}