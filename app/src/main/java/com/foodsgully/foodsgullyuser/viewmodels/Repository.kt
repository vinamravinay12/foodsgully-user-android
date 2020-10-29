package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class Repository(private val context : Context?){
    abstract fun getResponse() : MutableLiveData<APIResponse>

    fun getDefaultErrorMessage() = NiroAppUtils.getDefaultErrorMessage(context)

    fun getAPIInterface() = ApiClient.getAPIInterface(context)
}


