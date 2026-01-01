package com.example.shoppingapp.data.source.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.mapper.SellerMapper
import com.example.shoppingapp.data.remote.api.SellerAPI
import com.example.shoppingapp.domain.model.SellerInStore

class PagingSourceSeller(private val sellerAPI: SellerAPI) :
    PagingSource<Int, SellerInStore>() {

    override fun getRefreshKey(state: PagingState<Int, SellerInStore>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey
                ?: state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SellerInStore> {
        return try {
            val pageNumber = params.key ?: 0
            val response = sellerAPI.getAllSeller(pageNumber)
            if (response.isSuccessful) {
                val data = response.body()
                val listSeller = data?.contents?.map {
                    SellerMapper.mapToSellerInStore(it)
                } ?: emptyList()
                LoadResult.Page(
                    listSeller,
                    prevKey = if (pageNumber == 0) null else pageNumber - 1,
                    nextKey = if (listSeller.isEmpty()) null else pageNumber + 1
                )
            } else {
                LoadResult.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}