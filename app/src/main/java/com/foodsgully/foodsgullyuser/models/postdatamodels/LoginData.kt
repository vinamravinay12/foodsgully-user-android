package com.foodsgully.foodsgullyuser.models.postdatamodels

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("userName")
    val userName : String,

    @SerializedName("password")
    val password : String,

    @SerializedName("userType")
    val userType : String = "USER"
)