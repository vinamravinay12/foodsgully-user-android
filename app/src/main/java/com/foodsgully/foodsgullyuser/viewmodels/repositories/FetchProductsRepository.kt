package com.foodsgully.foodsgullyuser.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FetchProductsRepository<T>(private val categoryName : String,context: Context) : Repository(context) {

    override fun getResponse(): MutableLiveData<GenericResponse> {

        val responseData = MutableLiveData<GenericResponse>()

        val loader : GenericResponse = APILoader(true)
        val queryMap = hashMapOf(FoodsGullyConstants.ARG_CATEGORY to categoryName,FoodsGullyConstants.ARG_OFFSET to 0,FoodsGullyConstants.ARG_LIMIT to 80)
        getAPIInterface()?.getProductsPerCategory(queryMap)?.enqueue(object : Callback<GenericAPIResponse<List<Product>>> {

            override fun onFailure(call: Call<GenericAPIResponse<List<Product>>>, t: Throwable) {
                val response = APIError(404,t.localizedMessage)
                responseData.value = response
            }

            override fun onResponse(call: Call<GenericAPIResponse<List<Product>>>, response: Response<GenericAPIResponse<List<Product>>>) {

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


                val success = Success<T>(response.body()?.responseData as? T)
                responseData.value = success
            }

        })

        responseData.value = loader
        return responseData
    }
}