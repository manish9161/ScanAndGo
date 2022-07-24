package com.app.scanandgo.feature_scan.data

import com.app.scanandgo.feature_cart.data.CartItem
import com.app.scanandgo.feature_cart.data.CartItemDao
import com.app.scanandgo.network.ApiService
import com.app.scanandgo.network.BaseApiResponse
import com.app.scanandgo.network.NetworkResult
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    private val cartItemDao: CartItemDao
) : BaseApiResponse() {

    suspend fun getProduct(productId: Int): NetworkResult<ProductDto> {
        return safeApiCall { apiService.getProduct(productId) }
    }

    suspend fun addCartItem(cartItem: CartItem) {
        val cartItemExists = cartItemDao.getCartItem(cartItem.id)
        if(cartItemExists != null) {
            cartItem.quantity = cartItemExists.quantity + 1
            cartItem.total = cartItem.price * cartItem.quantity
            cartItemDao.updateCartItem(cartItem)
        } else {
            cartItemDao.insertCartItem(cartItem)
        }

    }
}