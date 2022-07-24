package com.app.scanandgo.network

import com.app.scanandgo.feature_scan.data.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDto>

}