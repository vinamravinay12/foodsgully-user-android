package com.foodsgully.foodsgullyuser.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Product

class ProductDetailsViewModel(private val product : Product?) : ViewModel() {


    fun geProductName() = MutableLiveData<String>(product?.description)


    fun getDiscount() = MutableLiveData<Int>().apply {

        this.value = ((product?.mrp?.toInt() ?: 0) - (product?.salePrice?.toInt() ?: 0))

    }

    fun getMrp()  = MutableLiveData<String>(product?.mrp)

    fun getQuantity() = MutableLiveData<String>().apply {
        this.value = "${product?.quantity} ${product?.quantityType}"
    }

    fun salePrice() = MutableLiveData<String>(product?.salePrice)
}