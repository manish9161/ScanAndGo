package com.app.scanandgo.feature_cart.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.app.scanandgo.db.StoreDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest // unit test
class CartDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var storeDatabase: StoreDatabase
    private lateinit var cartItemDao: CartItemDao

    @Before
    fun setup() {
        storeDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).allowMainThreadQueries().build()
        cartItemDao = storeDatabase.getCartItemDao()
    }

    @After
    fun teardown() {
        storeDatabase.close()
    }

    @Test
    fun insertCartItem() = runBlockingTest {
        val cartItem = CartItem(1, "James", 100.0, 1,100.0, "")
        cartItemDao.insertCartItem(cartItem)

        val cartItemCheck = cartItemDao.getCartItem(1)

        assertThat(cartItemCheck).isNotNull()
    }

    @Test
    fun deleteCartItem() = runBlockingTest {
        val cartItem = CartItem(1, "James", 100.0, 1,100.0, "")
        cartItemDao.insertCartItem(cartItem)
        cartItemDao.deleteCartItem(1)
        val cartItemCheck = cartItemDao.getCartItem(1)
        assertThat(cartItemCheck).isNull()
    }

}