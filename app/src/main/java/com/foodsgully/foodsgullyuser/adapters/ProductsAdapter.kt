package com.foodsgully.foodsgullyuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.ProductViewHolder
import com.foodsgully.foodsgullyuser.databinding.LayoutCardProductBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.Product

class ProductsAdapter(private val dataList : MutableList<Product>, private val layoutRes : Int) : BaseRecyclerAdapter<Product>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Product> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingProduct = DataBindingUtil.inflate<LayoutCardProductBinding>(inflater,layoutRes,parent,false)
        return ProductViewHolder(bindingProduct,getVariablesMap())
    }


}