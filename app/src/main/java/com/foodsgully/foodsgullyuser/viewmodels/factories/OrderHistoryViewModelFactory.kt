package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.OrderHistoryViewModel

class OrderHistoryViewModelFactory : AbstractViewModelFactory<OrderHistoryViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OrderHistoryViewModel() as T
    }
}