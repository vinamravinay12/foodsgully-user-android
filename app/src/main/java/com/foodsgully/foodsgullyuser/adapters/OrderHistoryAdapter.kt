package com.foodsgully.foodsgullyuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.OrderHistoryViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.OrderItemViewHolder
import com.foodsgully.foodsgullyuser.databinding.CardOrderHistoryBinding
import com.foodsgully.foodsgullyuser.databinding.CardOrderedItemBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderItem

class OrderHistoryAdapter(private val dataList : MutableList<Order>, private val layoutRes : Int) : BaseRecyclerAdapter<Order>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Order> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingOrderHistoryItem = DataBindingUtil.inflate<CardOrderHistoryBinding>(inflater,layoutRes,parent,false)
        return OrderHistoryViewHolder(bindingOrderHistoryItem,getVariablesMap())
    }


}