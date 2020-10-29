package com.foodsgully.foodsgullyuser.models

sealed class GenericResponse

data class APIError( val errorCode : Int,  val errorMessage : String) : GenericResponse()

data class Success<T>( val data : T? ) : GenericResponse()

data class APILoader( val isLoading : Boolean) : GenericResponse()