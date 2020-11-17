package com.foodsgully.foodsgullyuser.adapters.viewholders

import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.CardOrderedItemBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderItem

class OrderItemViewHolder(private val viewBinding : CardOrderedItemBinding) : BaseViewHolder<OrderItem>(viewBinding){


    override fun bind(item: OrderItem, position: Int) {

       viewBinding.orderItem = item
        viewBinding.tvOrderedQuantity.text = String.format(viewBinding.root.context.getString(R.string.txt_selected_quantity),item.quantitySelected.toString())
        viewBinding.tvOrderPrice.text = String.format(viewBinding.root.context.getString(R.string.txt_item_total),item.itemPrice.toString())

    }

}