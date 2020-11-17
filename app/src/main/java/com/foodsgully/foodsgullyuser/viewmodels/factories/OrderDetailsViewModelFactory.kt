package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.viewmodels.OrderDetailsViewModel

class OrderDetailsViewModelFactory(private val order : Order?) : AbstractViewModelFactory<OrderDetailsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Order::class.java).newInstance(order)
    }
}