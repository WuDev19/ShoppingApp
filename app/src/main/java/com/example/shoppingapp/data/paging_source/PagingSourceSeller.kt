package com.example.shoppingapp.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
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
            val offset = params.key ?: 0
            val response = sellerAPI.getAllSeller(offset)
            if (response.isSuccessful) {
                val data = response.body()
                val listSeller = data?.contents?.map {
                    SellerMapper.mapToSellerInStore(it)
                } ?: emptyList()
                LoadResult.Page(
                    listSeller,
                    prevKey = if (offset == 0) null else offset - 1,
                    nextKey = if (listSeller.isEmpty()) null else offset + 1
                )
            } else {
                LoadResult.Error(Exception("API ERROR"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}