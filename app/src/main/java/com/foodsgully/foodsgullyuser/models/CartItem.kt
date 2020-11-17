package com.foodsgully.foodsgullyuser.models

import com.foodsgully.foodsgullyuser.models.responsemodels.Product

data class CartItem(
    val selectedProduct : Product,
    var totalQuantity : Int = 0,
    var itemPrice : Double = 0.0
)
