package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdateAddressData
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.viewmodels.repositories.UpdateHomeAddressRepository

class AddressViewModel(homeAddress: HomeAddress?) : ViewModel() {

    private var homeAddressData = MutableLiveData<HomeAddress>()


    init {
        homeAddressData.value = homeAddress
    }

    fun getHomeAddress() = homeAddressData.value

    fun setHomeAddress(homeAddress: HomeAddress?) {
        homeAddressData.value = homeAddress
    }

    fun updateAddress(context: Context) : MutableLiveData<GenericResponse> {
        return UpdateHomeAddressRepository<String>(UpdateAddressData(homeAddressData.value),context).getResponse()
    }




}