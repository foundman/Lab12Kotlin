package com.example.lab12kotlin.data.remote

import com.example.lab12kotlin.data.model.Post
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Post>
}