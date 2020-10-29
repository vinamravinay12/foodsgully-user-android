package com.foodsgully.foodsgullyuser.viewmodels.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.network.ApiClient
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class Repository(private val context : Context?){
    abstract fun getResponse() : MutableLiveData<GenericResponse>

    fun getDefaultErrorMessage() = FoodsGullyUtils.getDefaultErrorMessage(context)

    fun getAPIInterface() = ApiClient.getAPIInterface(context)
}


