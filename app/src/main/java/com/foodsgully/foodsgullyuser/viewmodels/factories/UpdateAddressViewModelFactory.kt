package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.viewmodels.AddressViewModel

class UpdateAddressViewModelFactory(private val homeAddress: HomeAddress?) : AbstractViewModelFactory<AddressViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeAddress::class.java).newInstance(homeAddress)
    }
}