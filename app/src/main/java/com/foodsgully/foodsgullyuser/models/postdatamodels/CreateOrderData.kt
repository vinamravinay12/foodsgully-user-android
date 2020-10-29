package com.foodsgully.foodsgullyuser.models.postdatamodels

import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.models.responsemodels.Product

data class CreateOrderData(

    val paymentId : String,
    val paymentStatus : String,
    val paymentMode : String,
    val orderAmount : Float,
    val deliveryDate : String,
    val deliveryTime : String,
    val totalQuantities : Int,
    val items : List<Product>,
    val deliveryAddress : HomeAddress
)