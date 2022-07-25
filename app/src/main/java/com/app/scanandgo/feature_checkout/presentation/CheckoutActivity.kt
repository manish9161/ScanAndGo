package com.app.scanandgo.feature_checkout.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.scanandgo.R
import com.app.scanandgo.core.observeOnce
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
        setupToolbar()
        checkoutViewModel.total.observe(this) {
            binding.txtTotal.text = String.format(getString(R.string.total_price, it.toString()))
        }

        checkoutViewModel.paymentDone.observeOnce(this) {
            orderAgain()
        }

        setupButtons()
    }

    private fun setupToolbar() {
        supportActionBar?.title = getString(R.string.checkout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupButtons() {
        binding.btnMakePayment.setOnClickListener {
            checkoutViewModel.clearCart()
        }
    }

    private fun orderAgain() {
        val intent = Intent(this@CheckoutActivity, BarcodeScanActivity::class.java)
        startActivity(intent)
    }

}