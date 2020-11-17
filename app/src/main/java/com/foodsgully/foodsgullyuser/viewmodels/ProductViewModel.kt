package com.foodsgully.foodsgullyuser.viewmodels

import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.ProductsAdapter
import com.foodsgully.foodsgullyuser.models.responsemodels.Product

class ProductViewModel : ListViewModel<Product>() {

    private lateinit var adapter: ProductsAdapter

    private val productsList : MutableList<Product> = ArrayList<Product>().toMutableList()

    init {
        adapter = ProductsAdapter(productsList,getViewType())
    }

    fun getProductsList() = productsList


    override fun getViewType(): Int = R.layout.layout_card_product


    override fun updateList() {
        adapter.updateList(productsList)
    }

    override fun getAdapter(): BaseRecyclerAdapter<Product> = adapter
}