package com.foodsgully.foodsgullyuser.network;

object FoodsGullyAPI {


    const val FILE_UPLOAD_URL = "gs://niroapp.appspot.com/"
    const val LOGIN = "/api/user/login"
    const val SIGN_UP = "/api/user/signup"
    const val GET_CATEGORIES = "/api/product/getCategories"
    const val GET_PRODUCTS = "/api/product/category/getProducts"
    const val SEARCH_PRODUCTS = "/api/product/search/{query}"
    const val CREATE_ORDER = "/api/user/createOrder"
    const val GET_ALL_ORDERS_FOR_DATE = "/api/user/orders/allOrders/{date}"
    const val UPDATE_ADDRESS = "/api/user/address/update"
    const val UPDATE_PROFILE = "/api/user/profile/update"
    const val UPDATE_ORDER = "/api/order/update"
    const val RESET_PASSWORD = "/api/user/resetPassword"
    const val SET_FCMTOKEN = "api/user/setFirebaseToken"

}