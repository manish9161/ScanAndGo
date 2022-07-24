package com.app.scanandgo.feature_splash.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.scanandgo.databinding.ActivitySplashBinding
import com.app.scanandgo.feature_cart.presentation.CartActivity
import com.app.scanandgo.feature_scan.presentation.BarcodeScanActivity
import com.app.scanandgo.feature_splash.domain.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            delay(3000L)
            withContext(Dispatchers.Main) {
                goToNextActivity()
            }
        }
    }

    private fun goToNextActivity() {

        splashViewModel.total.observe(this) { total ->
            if(total > 0) {
                val intent = Intent(this@SplashActivity, CartActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, BarcodeScanActivity::class.java)
                startActivity(intent)
            }
            finish()
        }

        splashViewModel.getCartTotal()
    }

}