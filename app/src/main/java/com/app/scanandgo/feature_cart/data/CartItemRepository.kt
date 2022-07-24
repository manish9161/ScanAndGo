package com.app.scanandgo.feature_cart.data

import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

class CartItemRepository @Inject constructor(
    private val cartItemDao: CartItemDao
) {

    suspend fun getItemLists(): List<CartItem> {
        return cartItemDao.getCartItems()
    }

    suspend fun getCartTotal() : Double {
        val total = cartItemDao.getCartTotal()
        return total?.let {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            df.format(total).toDouble()
        } ?: 0.0

    }

    suspend fun clearCart() {
        cartItemDao.deleteCartItems()
    }

    suspend fun removeCartItem(id: Int) {
        cartItemDao.deleteCartItem(id)
    }

}