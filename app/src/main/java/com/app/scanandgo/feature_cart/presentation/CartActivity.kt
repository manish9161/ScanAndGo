package com.app.scanandgo.feature_cart.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.scanandgo.R
import com.app.scanandgo.core.RecyclerViewItemClick
import com.app.scanandgo.databinding.ActivityCartBinding
import com.app.scanandgo.feature_cart.data.CartItem
import com.app.scanandgo.feature_cart.domain.CartViewModel
import com.app.scanandgo.feature_checkout.presentation.CheckoutActivity
import com.app.scanandgo.feature_scan.presentation.BarcodeScanActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CartActivity: AppCompatActivity() {

    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var binding: ActivityCartBinding
    private var cartAdapter: CartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
        getTotal()
    }

    private fun getTotal() {
        cartViewModel.total.observe(this) { total ->
            setTotal(total)
        }
    }

    private fun setTotal(total: Double) {
        if(total > 0) {
            binding.btnCheckout.visibility = View.VISIBLE
            binding.txtTotal.visibility = View.VISIBLE
            binding.txtTotal.text = String.format(getString(R.string.total), total.toString())
        } else {
            binding.txtTotal.text = ""
            binding.txtTotal.visibility = View.GONE
            binding.btnCheckout.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter()
        binding.rvCart.adapter = cartAdapter
        cartViewModel.cartItemList.observe(this) { cartItemList ->
            updateRecyclerView(cartItemList)
        }

        cartAdapter?.setIRecyclerviewItemClick(object : RecyclerViewItemClick<CartItem> {
            override fun itemRemove(item: CartItem, position: Int) {
                lifecycleScope.launch(Dispatchers.IO) {
                    cartViewModel.removeItem(item.id)
                    withContext(Dispatchers.Main) {
                        cartAdapter?.let { adapter ->
                            adapter.removeItem(position)
                            if(adapter.cartItemList.isEmpty()) {
                                updateRecyclerView(adapter.cartItemList)
                            }
                        }

                    }
                }
            }

        })

    }

    private fun updateRecyclerView(cartItemList: List<CartItem>) {
        if(cartItemList.isNotEmpty()) {
            binding.txtSuggestion.visibility = View.GONE
            binding.txtEmpty.visibility = View.GONE
            binding.rvCart.visibility = View.VISIBLE
            cartAdapter?.addData(cartItemList)
        } else {
            binding.txtSuggestion.visibility = View.VISIBLE
            binding.txtEmpty.visibility = View.VISIBLE
            binding.rvCart.visibility = View.GONE
        }
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