package com.foodsgully.foodsgullyuser.viewmodels.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetAllOrdersForDateRepository<T>(private val date : String, private val context : Context?)  : Repository(context) {


    override fun getResponse(): MutableLiveData<GenericResponse> {
        val responseData = MutableLiveData<GenericResponse>()

        val loader: GenericResponse = APILoader(true)
        getAPIInterface()?.getAllOrders(date)
            ?.enqueue(object : Callback<GenericAPIResponse<List<Order>>> {

                override fun onFailure(call: Call<GenericAPIResponse<List<Order>>>, t: Throwable) {
                    Log.e("TAGSS", "failure " + t.message)
                    val response = APIError(404, t.localizedMessage)
                    responseData.value = response
                }

                override fun onResponse(
                    call: Call<GenericAPIResponse<List<Order>>>,
                    response: Response<GenericAPIResponse<List<Order>>>
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