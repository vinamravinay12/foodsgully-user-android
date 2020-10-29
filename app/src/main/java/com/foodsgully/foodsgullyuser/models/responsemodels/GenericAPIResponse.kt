package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GenericAPIResponse<T>(
    @SerializedName("success")
    val isSuccess: Boolean = false,
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("data")
    val responseData: T? = null
)
