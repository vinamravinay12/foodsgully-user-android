package com.foodsgully.foodsgullyuser.viewmodels

import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.CartItemsAdapter
import com.foodsgully.foodsgullyuser.models.CartItem
import java.util.*

class CartViewModel : ListViewModel<CartItem>() {


    private var cartItems = LinkedList<CartItem>().toMutableList()
    private var adapter : CartItemsAdapter

    init {
        adapter = CartItemsAdapter(cartItems,getViewType())
    }


    override fun getViewType(): Int = R.layout.layout_card_cart_item


    override fun updateList() {
       adapter.updateList(cartItems)
    }


    fun removeItemFromList(item : CartItem){
        adapter.removeItemFromList(item)
    }

    override fun getAdapter(): BaseRecyclerAdapter<CartItem>  = adapter

    fun getCartItems() = cartItems

    fun getTotalCost() = cartItems.map { it.itemPrice }.sum()

    fun getFinalCost() = getTotalCost() + 0.0

    fun getTotalItems() = cartItems.map { it.totalQuantity }.sum()



}