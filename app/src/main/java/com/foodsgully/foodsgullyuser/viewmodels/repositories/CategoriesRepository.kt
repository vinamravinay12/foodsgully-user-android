package com.foodsgully.foodsgullyuser.viewmodels.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CategoriesRepository<T>( private val context : Context?)  : Repository(context){


    override fun getResponse(): MutableLiveData<GenericResponse> {
        val responseData = MutableLiveData<GenericResponse>()

        val loader : GenericResponse = APILoader(true)

        responseData.value = loader

        if(checkIfLastSavedDateIsDifferent(context) && getSavedCategories(context).isNotEmpty()) {
            responseData.value = Success(getSavedCategories(context))

        } else {

            getAPIInterface()?.categories?.enqueue(object : Callback<GenericAPIResponse<List<Category>>> {

                override fun onFailure(call: Call<GenericAPIResponse<List<Category>>>, t: Throwable) {
                    Log.e("TAGSS","failure " + t.message)
                    val response = APIError(404,t.localizedMessage)
                    responseData.value = response
                }

                override fun onResponse(call: Call<GenericAPIResponse<List<Category>>>, response: Response<GenericAPIResponse<List<Category>>>) {

                    if(response.body()?.isSuccess != true) {
                        try {
                            val errorResponse = Gson().fromJson<GenericAPIResponse<Any>>(
                                response.errorBody()?.charStream(),
                                GenericAPIResponse::class.java
                            )
                            responseData.value = APIError(
                                response.code(),
                                if (!errorResponse.message.isNullOrEmpty()) errorResponse.message else getDefaultErrorMessage()
                            )
                            return
                        } catch (exception : Exception) {
                            responseData.value = APIError(response.code(), exception.localizedMessage)

                        }
                    }

                    saveCategories(response.body()?.responseData , FoodsGullyConstants.KEY_CATEGORIES,context = context)
                    val success = Success<T>(response.body()?.responseData as? T)
                    responseData.value = success
                }

            })
        }

        return responseData

    }

    private fun saveCategories(data : Any?,key : String,context: Context?) {
        if (context != null) {
            SharedPreferenceManager(context,FoodsGullyConstants.CATEGORIES_SP).storeLongPreference(FoodsGullyConstants.KEY_CATEGORIES_FETCHED_TIMESTAMP,System.currentTimeMillis())
            SharedPreferenceManager(context, FoodsGullyConstants.CATEGORIES_SP).storeComplexObjectPreference(key,data)
        }
    }

    private fun getSavedCategories(context: Context?) : List<Category> {
         if(context != null) {
             val categoriesJson = SharedPreferenceManager(context,FoodsGullyConstants.CATEGORIES_SP).getStringPreference(FoodsGullyConstants.KEY_CATEGORIES)

             if(!categoriesJson.isNullOrEmpty() && !categoriesJson.equals("null",true)) {
                 return Gson().fromJson<List<Category>>(categoriesJson, object: TypeToken<List<Category>>() {}.type)
             }

        }

        return ArrayList()
    }

    private fun checkIfLastSavedDateIsDifferent(context: Context?) : Boolean {
        val lastSavedTime = context?.let { SharedPreferenceManager(it,FoodsGullyConstants.CATEGORIES_SP).getLongPreference(FoodsGullyConstants.KEY_CATEGORIES_FETCHED_TIMESTAMP,
            Long.MIN_VALUE) } ?: Long.MIN_VALUE

        return if(lastSavedTime != Long.MIN_VALUE) {
            val savedDateString = FoodsGullyUtils.getDateString(Date(lastSavedTime),FoodsGullyConstants.POST_DATE_FORMAT)
            val currentDateString = FoodsGullyUtils.getDateString(Date(),FoodsGullyConstants.POST_DATE_FORMAT)

            savedDateString.equals(currentDateString)

        } else false

    }
}