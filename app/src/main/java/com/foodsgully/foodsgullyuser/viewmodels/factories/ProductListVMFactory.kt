package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.viewmodels.ProductListViewModel

class ProductListVMFactory : AbstractViewModelFactory<ProductListViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductListViewModel() as T
    }
}