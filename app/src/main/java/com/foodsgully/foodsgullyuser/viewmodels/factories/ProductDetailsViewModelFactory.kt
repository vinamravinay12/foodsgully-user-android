package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.viewmodels.ProductDetailsViewModel

class ProductDetailsViewModelFactory(private val product : Product?) : AbstractViewModelFactory<ProductDetailsViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.getConstructor(Product::class.java).newInstance(product)
    }
}