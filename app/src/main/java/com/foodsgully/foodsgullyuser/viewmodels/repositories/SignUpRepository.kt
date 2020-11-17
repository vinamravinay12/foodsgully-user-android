package com.foodsgully.foodsgullyuser.viewmodels.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.postdatamodels.SignupData
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpRepository<T>(private val signUpData: SignupData, private val context : Context?)  : Repository(context){


    override fun getResponse(): MutableLiveData<GenericResponse> {
        val responseData = MutableLiveData<GenericResponse>()

        val loader : GenericResponse = APILoader(true)
        getAPIInterface()?.signUp(signUpData)?.enqueue(object : Callback<GenericAPIResponse<User>> {

            override fun onFailure(call: Call<GenericAPIResponse<User>>, t: Throwable) {
                Log.e("TAGSS","failure " + t.message)
                val response = APIError(404,t.localizedMessage)
                responseData.value = response
            }

            override fun onResponse(call: Call<GenericAPIResponse<User>>, response: Response<GenericAPIResponse<User>>) {

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

                storeData(response.body()?.token , FoodsGullyConstants.USER_TOKEN,context = context)
                storeData(response.body()?.responseData, FoodsGullyConstants.USER_DATA,context)
                val success = Success<T>(response.body()?.responseData as? T)
                responseData.value = success
            }

        })

        responseData.value = loader
        return responseData

    }

    private fun storeData(data : Any?,key : String,context: Context?) {
        if (context != null) {
            SharedPreferenceManager(context, FoodsGullyConstants.LOGIN_SP).storeComplexObjectPreference(key,data)
        }
    }
}