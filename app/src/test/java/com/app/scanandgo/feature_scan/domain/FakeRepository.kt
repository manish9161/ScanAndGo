package com.app.scanandgo.feature_scan.domain

import com.app.scanandgo.feature_scan.data.ProductDto
import com.app.scanandgo.network.BaseApiResponse
import com.app.scanandgo.network.NetworkResult
import retrofit2.Response

class FakeRepository: BaseApiResponse() {

    suspend fun getProduct(): NetworkResult<ProductDto> {

        val productDto = FakeResponses.convertJsonToClass(
            FakeResponses.fakeCVJsonResponse200,
            ProductDto::class.java
        )

        val mockResponse = Response.success(productDto)

        return safeApiCall { mockResponse }
    }

}