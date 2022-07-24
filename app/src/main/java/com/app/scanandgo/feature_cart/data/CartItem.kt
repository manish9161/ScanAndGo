package com.app.scanandgo.feature_cart.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class CartItem(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
    var total: Double,
    val image: String?
)