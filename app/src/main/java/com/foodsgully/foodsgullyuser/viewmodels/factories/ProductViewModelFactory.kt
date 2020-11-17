package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.viewmodels.ProductListViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ProductViewModel

class ProductViewModelFactory() : AbstractViewModelFactory<ProductViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductViewModel() as T
    }
}