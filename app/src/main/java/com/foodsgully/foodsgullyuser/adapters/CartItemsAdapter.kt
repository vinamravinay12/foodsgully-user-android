package com.foodsgully.foodsgullyuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.CartViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.ProductViewHolder
import com.foodsgully.foodsgullyuser.databinding.LayoutCardCartItemBinding
import com.foodsgully.foodsgullyuser.databinding.LayoutCardProductBinding
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.responsemodels.Product

class CartItemsAdapter(private val dataList : MutableList<CartItem>, private val layoutRes : Int) : BaseRecyclerAdapter<CartItem>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CartItem> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingCartItem = DataBindingUtil.inflate<LayoutCardCartItemBinding>(inflater,layoutRes,parent,false)
        return CartViewHolder(bindingCartItem,getVariablesMap())
    }

    fun removeItemFromList(item : CartItem) {
        val index  = dataList.indexOfFirst { it.selectedProduct.productId.equals(item.selectedProduct.productId) }
        if(index >= 0) {
            dataList.removeAt(index)
            notifyItemRemoved(index)
        }

    }


}