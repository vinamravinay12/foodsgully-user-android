package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.viewmodels.CheckoutViewModel
import com.foodsgully.foodsgullyuser.viewmodels.LoginViewModel

class CheckoutViewModelFactory(private val totalAmount : Double) : AbstractViewModelFactory<CheckoutViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Double::class.java).newInstance(totalAmount)
    }
}