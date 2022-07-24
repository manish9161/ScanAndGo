package com.app.scanandgo.feature_cart.data

import androidx.room.*

@Dao
interface CartItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Query("select * from cartitem")
    suspend fun getCartItems(): List<CartItem>

    @Query("select * from cartitem where id=:id")
    suspend fun getCartItem(id: Int): CartItem?

    @Query("select sum(total) from cartitem")
    suspend fun getCartTotal(): Double

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Query("delete from cartitem where id=:id")
    suspend fun deleteCartItem(id: Int)

    @Query("delete from cartitem")
    suspend fun deleteCartItems()

}