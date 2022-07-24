package com.app.scanandgo.feature_cart.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.scanandgo.R
import com.app.scanandgo.core.RecyclerViewItemClick
import com.app.scanandgo.databinding.ChildCartItemBinding
import com.app.scanandgo.feature_cart.data.CartItem
import com.bumptech.glide.Glide

class CartAdapter: RecyclerView.Adapter<CartAdapter.ViewHolder>() {

	var cartItemList: MutableList<CartItem> = mutableListOf()
	private var iRecyclerViewItemClick: RecyclerViewItemClick<CartItem>? = null

	fun setIRecyclerviewItemClick(recyclerViewItemClick: RecyclerViewItemClick<CartItem>) {
		iRecyclerViewItemClick = recyclerViewItemClick
	}

	inner class ViewHolder(val binding: ChildCartItemBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ChildCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		with(holder){
			with(cartItemList[position]){
				binding.txtName.text = this.name
				val price = String.format(binding.txtPrice.context.getString(R.string.Rs, this.price.toString()))
				val quantity = String.format(binding.txtPrice.context.getString(R.string.quantity, this.quantity.toString()))
				binding.txtPrice.text = price
				binding.txtQuantity.text = quantity
				Glide.with(binding.root.context).load(this.image).into(binding.imgCartItem)

				binding.txtRemove.setOnClickListener {
					iRecyclerViewItemClick?.itemRemove(cartItemList[position], position)
				}
			}


		}
	}


	override fun getItemCount(): Int = cartItemList.size

	fun addData(list: List<CartItem>) {
		cartItemList.clear()
		cartItemList.addAll(list)
		notifyItemRangeChanged(0, cartItemList.size - 1)
	}

	fun removeItem(position: Int) {
		cartItemList.removeAt(position)
		notifyItemRemoved(position)
	}
}
