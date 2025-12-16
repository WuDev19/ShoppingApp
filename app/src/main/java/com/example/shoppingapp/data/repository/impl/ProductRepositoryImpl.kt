package com.example.shoppingapp.data.repository.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.local.db.AppDatabase
import com.example.shoppingapp.data.local.mapper.ProductEntityMapper
import com.example.shoppingapp.data.remote.mapper.ProductDTOMapper
import com.example.shoppingapp.data.remote.api.ProductAPI
import com.example.shoppingapp.data.source.paging_source.PagingSourceOtherProductOfShop
import com.example.shoppingapp.data.source.paging_source.PagingSourceProduct
import com.example.shoppingapp.data.source.paging_source.PagingSourceProductByCategory
import com.example.shoppingapp.data.source.paging_source.PagingSourceRelatedProduct
import com.example.shoppingapp.data.source.paging_source.PagingSourceSearchProduct
import com.example.shoppingapp.data.repository.mediator.AllProductOfShopRemoteMediator
import com.example.shoppingapp.data.repository.mediator.BestSellerProductShopRemoteMediator
import com.example.shoppingapp.data.repository.mediator.CategoryAndProductShopRemoteMediator
import com.example.shoppingapp.data.repository.mediator.SearchProductInShopRemoteMediator
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import com.example.shoppingapp.domain.model.product.ProductDetail
import com.example.shoppingapp.domain.repository.product.ProductRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI,
    private val appDatabase: AppDatabase,
) :
    ProductRepository {

    override fun getProducts(): Flow<PagingData<ProductDisplay>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PagingSourceProduct(productAPI) }
        ).flow
    }

    override fun getProductByCategory(theLoai: String): Flow<PagingData<ProductDisplay>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceProductByCategory(productAPI, theLoai)
            }
        ).flow
    }

    override fun searchProduct(text: String): Flow<PagingData<ProductDisplay>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
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
                val data = ProductDTOMapper.mapToProductDetail(it)
                emit(StateListener.Success(data))
            } ?: emit(StateListener.Error(Exception("Cast Error")))
        } else {
            emit(StateListener.Error(Exception("Can't not find product")))
        }
    }.flowOn(Dispatchers.IO)

    override fun getOtherProductsOfShop(id: Int): Flow<PagingData<ProductDisplay>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceOtherProductOfShop(productAPI, id)
            }
        ).flow
    }

    override fun getSanPhamRelated(id: Int): Flow<PagingData<ProductDisplay>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceRelatedProduct(productAPI, id)
            }
        ).flow
    }

    override fun getSanPhamChiTiet(id: Int): Flow<StateListener<List<ProductChiTiet>>> = flow {
        val response = productAPI.ganSanPhamChiTiet(id)
        if (response.isSuccessful) {
            val data = response.body()
            val listProductChiTiet = data?.map {
                ProductDTOMapper.mapToProductChiTiet(it)
            } ?: emptyList()
            emit(StateListener.Success(listProductChiTiet))
        } else {
            emit(StateListener.Error(Exception("Api Error")))
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    override fun timKiemSanPhamCuaShop(id: Int, text: String): Flow<PagingData<ProductDisplay>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = SearchProductInShopRemoteMediator(productAPI, appDatabase, id, text),
            pagingSourceFactory = {
                appDatabase.productDao().getPagingSourceSearchProductShop(id, text)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                ProductEntityMapper.mapToProduct(it)
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllProductOfShop(id: Int): Flow<PagingData<ProductDisplay>> {
        val queryKey = "Shop_$id"
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = AllProductOfShopRemoteMediator(productAPI, appDatabase, queryKey, id),
            pagingSourceFactory = {
                appDatabase.productDao().pagingSourceProductShop(id)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                ProductEntityMapper.mapToProduct(it)
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getBestSellerProductByShop(id: Int): Flow<PagingData<ProductDisplay>> {
        val queryKey = "Best_Seller_Shop_$id"
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            remoteMediator = BestSellerProductShopRemoteMediator(
                productAPI,
                appDatabase,
                id,
                queryKey
            ),
            pagingSourceFactory = {
                appDatabase.productDao().pagingSourceBestSellerProductShop(id)
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                ProductEntityMapper.mapToProduct(entity)
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getCategoryAndProductByShop(
        id: Int,
        tenDanhMuc: String
    ): Flow<PagingData<ProductDisplay>> {
        val queryKey = "Category_Product_Shop_${id}_$tenDanhMuc"
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            remoteMediator = CategoryAndProductShopRemoteMediator(
                productAPI,
                appDatabase,
                queryKey,
                id,
                tenDanhMuc
            ),
            pagingSourceFactory = {
                appDatabase.categoryAndProductDao()
                    .pagingSourceCategoryAndProductShop(id, tenDanhMuc)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                ProductEntityMapper.mapToProduct(it)
            }
        }
    }

}