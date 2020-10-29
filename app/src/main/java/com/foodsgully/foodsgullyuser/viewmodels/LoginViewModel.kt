package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.foodsgullyuser.R
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail


class LoginViewModel : ViewModel() {

    private var email = MutableLiveData<String>()
    private var password = MutableLiveData<String>()

    private var emailError = MutableLiveData<String>()
    private var passwordError = MutableLiveData<String>()


    fun getEmail() = email
    fun getPassword() = password
    fun getEmailError() = emailError
    fun getPasswordError() = passwordError
    fun validateEmail(context: Context)  : Boolean {

        return when {
            email.value.isNullOrEmpty() -> {
                emailError.value = context.getString(R.string.error_empty_email)
                false
            }
            email.value?.validEmail() == false -> {
                emailError.value = context.getString(R.string.invalid_email)
                false
            }
            else -> {
                emailError.value = ""
                true
            }
        }
    }

    fun validatePassword(context: Context) : Boolean {
        when {
            password.value.isNullOrEmpty() -> {
                passwordError.value = context.getString(R.string.error_empty_password)
                return false
            }

            else -> passwordError.value = ""
        }
        return true

    }


    fun validateLogin(context: Context) : Boolean {
        return validateEmail(context) && validatePassword(context)
    }

    fun loginUser() {

    }

}