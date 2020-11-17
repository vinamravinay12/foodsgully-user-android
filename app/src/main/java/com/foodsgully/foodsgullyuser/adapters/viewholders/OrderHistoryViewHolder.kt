package com.foodsgully.foodsgullyuser.adapters.viewholders

import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.CardOrderHistoryBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.Order

class OrderHistoryViewHolder(private val viewBinding : CardOrderHistoryBinding,private val variables : HashMap<Int,Any?>) : BaseViewHolder<Order>(viewBinding){


    override fun bind(item: Order, position: Int) {

        viewBinding.orderDetails = item.orderDetails
        setVariables(variables)

        val context = viewBinding.root.context
        val itemSuffix = if(item.orderDetails?.orderItems?.size  ?: 0 < 1) context.getString(R.string.txt_item) else context.getString(R.string.txt_items)
        viewBinding.tvTotalItems.text = "${item.orderDetails?.orderItems?.size ?: 0} $itemSuffix"

        val sum = item.orderDetails?.orderItems?.map { it.itemPrice }?.sum()
        viewBinding.tvGrandTotal.text = String.format(viewBinding.root.context.getString(R.string.txt_order_total), sum)
        viewBinding.tvPaymentStatus.text = String.format(viewBinding.root.context.getString(R.string.payment_status),item.orderDetails?.paymentStatus.toString().capitalize())

        viewBinding.root.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }
    }

}