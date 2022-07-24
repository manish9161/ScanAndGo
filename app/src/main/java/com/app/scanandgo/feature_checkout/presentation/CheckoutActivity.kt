package com.app.scanandgo.feature_checkout.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.scanandgo.R
import com.app.scanandgo.databinding.ActivityCheckoutBinding
import com.app.scanandgo.feature_checkout.domain.CheckoutViewModel
import com.app.scanandgo.feature_scan.presentation.BarcodeScanActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkoutViewModel.total.observe(this) {
            binding.txtTotal.text = String.format(getString(R.string.total_price, it.toString()))
        }

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnMakePayment.setOnClickListener {

            checkoutViewModel.clearCart()

            finish()
            val intent = Intent(this@CheckoutActivity, BarcodeScanActivity::class.java)
            startActivity(intent)
        }
    }

}