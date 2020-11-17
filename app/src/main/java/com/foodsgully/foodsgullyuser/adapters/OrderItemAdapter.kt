package com.foodsgully.foodsgullyuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.OrderItemViewHolder
import com.foodsgully.foodsgullyuser.databinding.CardOrderedItemBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderItem

class OrderItemAdapter(private val dataList : MutableList<OrderItem>, private val layoutRes : Int) : BaseRecyclerAdapter<OrderItem>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<OrderItem> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingOrderItem = DataBindingUtil.inflate<CardOrderedItemBinding>(inflater,layoutRes,parent,false)
        return OrderItemViewHolder(bindingOrderItem)
    }


}