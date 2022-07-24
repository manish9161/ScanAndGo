package com.app.scanandgo.feature_cart.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.scanandgo.core.LogUtils
import com.app.scanandgo.feature_cart.data.CartItem
import com.app.scanandgo.feature_cart.data.CartItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository
): ViewModel() {

    private val _cartItemList: MutableLiveData<List<CartItem>> = MutableLiveData<List<CartItem>>()
    val cartItemList: LiveData<List<CartItem>> = _cartItemList

    private val _total: MutableLiveData<Double> = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    init {
        getCartItems()
        getTotal()
    }


    private fun getCartItems() {
        viewModelScope.launch {
            _cartItemList.value = cartItemRepository.getItemLists()
        }
    }

    fun removeItem(id: Int) {
        viewModelScope.launch {
            cartItemRepository.removeCartItem(id)
            getTotal()
        }
    }

    private fun getTotal() {
        viewModelScope.launch {
            _total.value = cartItemRepository.getCartTotal()
            LogUtils.printError("total:: " + _total.value.toString())
        }
    }

}