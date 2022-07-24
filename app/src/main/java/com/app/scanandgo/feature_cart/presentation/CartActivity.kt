package com.app.scanandgo.feature_cart.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.scanandgo.core.RecyclerViewItemClick
import com.app.scanandgo.databinding.ActivityCartBinding
import com.app.scanandgo.feature_cart.data.CartItem
import com.app.scanandgo.feature_cart.domain.CartViewModel
import com.app.scanandgo.feature_checkout.presentation.CheckoutActivity
import com.app.scanandgo.feature_scan.presentation.BarcodeScanActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity: AppCompatActivity() {

    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var binding: ActivityCartBinding
    private var rvAdapter: RvAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        rvAdapter = RvAdapter()
        binding.rvCart.adapter = rvAdapter
        cartViewModel.cartItemList.observe(this) {
            if(it.isNotEmpty()) {
                binding.txtEmpty.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                rvAdapter?.addData(it)
            } else {
                binding.txtEmpty.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
            }
        }

        rvAdapter?.setIRecyclerviewItemClick(object : RecyclerViewItemClick<CartItem> {
            override fun itemRemove(item: CartItem, position: Int) {
                lifecycleScope.launch(Dispatchers.IO) {
//                    cartViewModel.removeItem()
//                    withContext(Dispatchers.Main) {
//
//                    }
                }
            }

        })

    }

    private fun setupButtons() {
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddItem.setOnClickListener {
            val intent = Intent(this, BarcodeScanActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}