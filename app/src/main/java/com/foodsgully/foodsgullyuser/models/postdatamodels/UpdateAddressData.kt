package com.foodsgully.foodsgullyuser.models.postdatamodels

import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress

data class UpdateAddressData(
    val homeAddress: HomeAddress? = null
)


data class UpdateProfileData(
    val fullName : String?,
    val phoneNumber : String?,
    val imageUrl : String?
)