package com.foodsgully.foodsgullyuser.network;


import com.foodsgully.foodsgullyuser.models.postdatamodels.CreateOrderData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.LoginData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.SignupData;
import com.foodsgully.foodsgullyuser.models.responsemodels.Category;
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse;
import com.foodsgully.foodsgullyuser.models.responsemodels.Product;
import com.foodsgully.foodsgullyuser.models.responsemodels.User;

import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiInterface {


    @GET(FoodsGullyAPI.LOGIN)
    Call<GenericAPIResponse<User>> login(@Body LoginData loginData);

    @GET(FoodsGullyAPI.GET_CATEGORIES)
    Call<GenericAPIResponse<Category>> getCategories();

    @GET(FoodsGullyAPI.GET_PRODUCTS)
    Call<GenericAPIResponse<Product>> getProductsPerCategory(@QueryMap HashMap<String,String> queryMap);

    @POST(FoodsGullyAPI.SIGN_UP)
    Call<GenericAPIResponse<User>> signUp(@Body SignupData signUpPostData);

    @GET(FoodsGullyAPI.CREATE_ORDER)
    Call<GenericAPIResponse> createOrder(@Body CreateOrderData createOrderData);





}

