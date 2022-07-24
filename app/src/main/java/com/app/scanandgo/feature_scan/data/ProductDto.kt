package com.app.scanandgo.feature_scan.data

import com.app.scanandgo.feature_cart.data.CartItem

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String?) {


    fun toCartItem(quantity: Int = 1): CartItem {
        return CartItem(
            id = id,
            name = title,
            price = price,
            quantity = quantity,
            total = (price * quantity),
            image = image
        )
    }

}