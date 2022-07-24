package com.app.scanandgo.feature_splash.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.scanandgo.feature_cart.data.CartItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository
): ViewModel() {

    private val _total: MutableLiveData<Double> = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    fun getCartTotal() {
        viewModelScope.launch {
            _total.value = cartItemRepository.getCartTotal()
        }
    }
}