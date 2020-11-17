package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.CartViewModel
import com.foodsgully.foodsgullyuser.viewmodels.LoginViewModel

class CartViewModelFactory: AbstractViewModelFactory<CartViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CartViewModel() as T
    }
}