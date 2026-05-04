package com.example.lab12kotlin.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.lab12kotlin.data.model.Post

class PostPagingSource(
    private val api: PostApi
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 1
            val loadSize = params.loadSize

            Log.d("PostPaging", "Загрузка страницы $page (по $loadSize элементов)")

            val response = api.getPosts(page = page, limit = loadSize)

            Log.d("PostPaging", "Страница $page загружена: ${response.size} постов")

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                // JSONPlaceholder имеет 100 постов, при limit=20 это 5 страниц
                nextKey = if (response.size == loadSize && page < 5) page + 1 else null
            )
        } catch (e: Exception) {
            Log.e("PostPaging", "Ошибка загрузки страницы", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}