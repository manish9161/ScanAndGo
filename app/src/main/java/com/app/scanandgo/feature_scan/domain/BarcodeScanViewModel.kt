package com.app.scanandgo.feature_scan.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.scanandgo.feature_cart.data.CartItem
import com.app.scanandgo.feature_scan.data.ProductDto
import com.app.scanandgo.feature_scan.data.ProductRepository
import com.app.scanandgo.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BarcodeScanViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _productResult: MutableLiveData<NetworkResult<ProductDto>> = MutableLiveData()
    val productResult: LiveData<NetworkResult<ProductDto>> = _productResult

    private val _itemAdded: MutableLiveData<Boolean> = MutableLiveData()
    val itemAdded: LiveData<Boolean> = _itemAdded

    fun fetchProduct(productId: Int) {
        viewModelScope.launch {
            val response = productRepository.getProduct(productId)
            _productResult.value = response
        }
    }

    fun addCartItem(cartItem: CartItem) {
        viewModelScope.launch {
            productRepository.addCartItem(cartItem)
            _itemAdded.value = true
        }
    }
}