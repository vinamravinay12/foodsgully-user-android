package com.foodsgully.foodsgullyuser.viewmodels.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdateAddressData
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateHomeAddressRepository<T>(private val updateAddressData: UpdateAddressData, private val context : Context?)  : Repository(context) {


    override fun getResponse(): MutableLiveData<GenericResponse> {
        val responseData = MutableLiveData<GenericResponse>()

        val loader: GenericResponse = APILoader(true)
        getAPIInterface()?.updateHomeAddress(updateAddressData)
            ?.enqueue(object : Callback<GenericAPIResponse<String>> {

                override fun onFailure(call: Call<GenericAPIResponse<String>>, t: Throwable) {
                    Log.e("TAGSS", "failure " + t.message)
                    val response = APIError(404, t.localizedMessage)
                    responseData.value = response
                }

                override fun onResponse(
                    call: Call<GenericAPIResponse<String>>,
                    response: Response<GenericAPIResponse<String>>
                ) {

                    if (response.body()?.isSuccess != true) {
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
                        } catch (exception: Exception) {
                            responseData.value =
                                APIError(response.code(), exception.localizedMessage)

                        }
                    }


                    val success = Success<T>(response.body()?.responseData as? T)
                    responseData.value = success
                }

            })

        responseData.value = loader
        return responseData

    }
}