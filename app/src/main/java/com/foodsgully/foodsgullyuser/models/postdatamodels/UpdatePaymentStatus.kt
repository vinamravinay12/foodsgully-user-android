package com.foodsgully.foodsgullyuser.models.postdatamodels

import retrofit2.http.Body

data class UpdatePaymentStatus(
    val paymentId : String? = null,
    val paymentSuccess : Boolean = false

)