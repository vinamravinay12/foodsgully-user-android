package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.CategoryAdapter
import com.foodsgully.foodsgullyuser.adapters.ProductSearchAdapter
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.viewmodels.repositories.SearchProductsRepository

class ProductSearchViewModel : ListViewModel<Product>() {


    private val products = ArrayList<Product>().toMutableList()

    private lateinit var productsAdapter: ProductSearchAdapter


    init {
        productsAdapter = ProductSearchAdapter(products, getViewType())
    }

    fun setProducts(products: List<Product>?) {
        this.products.clear()
        products?.toMutableList()?.let { this.products.addAll(it) }
        updateList()
    }

    override fun getViewType(): Int = R.layout.card_product_search

    override fun updateList() {
        productsAdapter.updateList(products)
    }

    override fun getAdapter(): BaseRecyclerAdapter<Product> = productsAdapter


    fun searchProducts(query: String, context: Context): MutableLiveData<GenericResponse> {

        return SearchProductsRepository<List<Product>>(query, context).getResponse()
    }
}