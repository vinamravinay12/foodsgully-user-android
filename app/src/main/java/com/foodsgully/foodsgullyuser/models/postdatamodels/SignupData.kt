package com.foodsgully.foodsgullyuser.models.postdatamodels

import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.google.gson.annotations.SerializedName

data class SignupData(
    @SerializedName("email")
    val email : String,

    @SerializedName("password")
    val password : String,

    @SerializedName("userType")
    val userType : String = "USER",

    @SerializedName("fullName")
    val fullName : String,

    @SerializedName("phoneNumber")
    val phoneNumber : String,

    @SerializedName("homeAddress")
    val homeAddress : HomeAddress? = null

)