package com.app.scanandgo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.scanandgo.feature_cart.data.CartItem
import com.app.scanandgo.feature_cart.data.CartItemDao

@Database(entities = [CartItem::class], exportSchema = false, version = 1)
abstract class StoreDatabase: RoomDatabase() {
    abstract fun getCartItemDao(): CartItemDao
}