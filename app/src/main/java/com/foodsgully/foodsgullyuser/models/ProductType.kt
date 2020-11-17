package com.foodsgully.foodsgullyuser.models

import com.foodsgully.foodsgullyuser.models.responsemodels.Product

data class ProductType(
    val type : String,
    val products : List<Product>,
    var isExpanded : Boolean = true
) {
    fun hasMoreThanOneProducts() = products.size > 1
}