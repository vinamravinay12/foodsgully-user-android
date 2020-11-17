package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ProductListViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ProductSearchViewModel

class ProductSearchViewModelFactory : AbstractViewModelFactory<ProductSearchViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductSearchViewModel() as T
    }
}