package com.example.lab12kotlin.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.lab12kotlin.data.model.Post
import com.example.lab12kotlin.data.remote.PostPagingSource
import com.example.lab12kotlin.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

object PostPagingProvider {

    private val api = RetrofitClient.postApi

    fun getPostsFlow(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,                    // Размер страницы
                enablePlaceholders = false,       // Без заглушек для простоты
                prefetchDistance = 5              // Подгружать за 5 элементов до конца
            ),
            pagingSourceFactory = {
                PostPagingSource(api)
            }
        ).flow
    }
}