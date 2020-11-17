package com.foodsgully.foodsgullyuser.network;


import com.foodsgully.foodsgullyuser.models.postdatamodels.CreateOrderData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.LoginData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.SetTokenData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.SignupData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdateAddressData;
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdatePaymentStatus;
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdateProfileData;
import com.foodsgully.foodsgullyuser.models.responsemodels.Category;
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse;
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress;
import com.foodsgully.foodsgullyuser.models.responsemodels.Order;
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderDetails;
import com.foodsgully.foodsgullyuser.models.responsemodels.Product;
import com.foodsgully.foodsgullyuser.models.responsemodels.User;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {


    @POST(FoodsGullyAPI.LOGIN)
    Call<GenericAPIResponse<User>> login(@Body LoginData loginData);

    @PUT(FoodsGullyAPI.RESET_PASSWORD)
    Call<GenericAPIResponse<String>> resetPassword(@Body LoginData loginData);

    @GET(FoodsGullyAPI.GET_CATEGORIES)
    Call<GenericAPIResponse<List<Category>>> getCategories();

    @GET(FoodsGullyAPI.GET_PRODUCTS)
    Call<GenericAPIResponse<List<Product>>> getProductsPerCategory(@QueryMap HashMap<String,Object> queryMap);

    @GET(FoodsGullyAPI.SEARCH_PRODUCTS)
    Call<GenericAPIResponse<List<Product>>> searchProducts(@Path("query") String query);


    @POST(FoodsGullyAPI.SIGN_UP)
    Call<GenericAPIResponse<User>> signUp(@Body SignupData signUpPostData);

    @POST(FoodsGullyAPI.CREATE_ORDER)
    Call<GenericAPIResponse<String>> createOrder(@Body CreateOrderData createOrderData);

    @GET(FoodsGullyAPI.GET_ALL_ORDERS_FOR_DATE)
    Call<GenericAPIResponse<List<Order>>> getAllOrders(@Path("date") String deliveryDate);

    @PUT(FoodsGullyAPI.UPDATE_ADDRESS)
    Call<GenericAPIResponse<String>> updateHomeAddress(@Body UpdateAddressData updateAddressData);

    @PUT(FoodsGullyAPI.UPDATE_PROFILE)
    Call<GenericAPIResponse<String>> updateProfile(@Body UpdateProfileData updateProfileData);

    @PUT(FoodsGullyAPI.UPDATE_ORDER)
    Call<GenericAPIResponse<String>> updateOrder(@Body UpdatePaymentStatus updatePaymentStatus);

    @POST(FoodsGullyAPI.SET_FCMTOKEN)
    Call<GenericAPIResponse<String>> setFCMToken(@Body SetTokenData setTokenData);






}

