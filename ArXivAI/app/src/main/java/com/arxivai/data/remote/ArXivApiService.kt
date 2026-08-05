package com.arxivai.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ArXivApiService {

    @GET("api/query")
    suspend fun searchPapers(
        @Query("search_query") searchQuery: String,
        @Query("start") start: Int = 0,
        @Query("max_results") maxResults: Int = 50,
        @Query("sortBy") sortBy: String = "submittedDate",
        @Query("sortOrder") sortOrder: String = "descending"
    ): ArXivFeedResponse

    @GET("api/query")
    suspend fun getPaperById(
        @Query("id_list") idList: String
    ): ArXivFeedResponse

    companion object {
        private const val BASE_URL = "https://export.arxiv.org/"

        fun create(): ArXivApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(ArXivApiService::class.java)
        }
    }
}