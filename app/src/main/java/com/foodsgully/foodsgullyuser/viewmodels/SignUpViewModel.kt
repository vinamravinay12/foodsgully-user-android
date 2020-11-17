package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.postdatamodels.SignupData
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.viewmodels.repositories.SignUpRepository
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import com.wajahatkarim3.easyvalidation.core.view_ktx.validNumber

class SignUpViewModel : ViewModel() {

    private var email = MutableLiveData<String>()
    private var password = MutableLiveData<String>()
    private var fullName = MutableLiveData<String>()
    private var phoneNumber = MutableLiveData<String>()
    private var homeAddress = MutableLiveData<HomeAddress>()

    private var emailError = MutableLiveData<String>()
    private var passwordError = MutableLiveData<String>()
    private var fullNameError = MutableLiveData<String>()
    private var phoneNumberError = MutableLiveData<String>()
    private var houseNumber = MutableLiveData<String>()
    private var floorNumber = MutableLiveData<String>()
    private var landmark = MutableLiveData<String>()

    fun getEmail() = email
    fun getPassword() = password
    fun getEmailError() = emailError
    fun getPasswordError() = passwordError
    fun getHomeAddress() = homeAddress.value
    fun getFullName() = fullName
    fun getPhoneNumber() = phoneNumber
    fun getFullNameError() = fullNameError
    fun getPhoneNumberError() = phoneNumberError
    fun getHouseNumber() = houseNumber
    fun getFloorNumber() = floorNumber
    fun getLandmark() = landmark
    fun zip() = homeAddress.value?.zip ?: ""


    fun setHomeAddress(address: HomeAddress){
        this.homeAddress.value = address
    }
    fun getStreetAddress() = MutableLiveData<String>().apply {
        this.value =  "${homeAddress.value?.streetAddress}, ${homeAddress.value?.city}".removePrefix(",").removeSuffix(",").trim()
        return this
    }



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


    fun validatePhoneNumber(context: Context) : Boolean {
        when {
            phoneNumber.value.isNullOrEmpty() -> {
                phoneNumberError.value = context.getString(R.string.error_empty_phonenumber)
                return false
            }

            phoneNumber.value?.validNumber() == false && phoneNumber.value?.length != 10 -> {
                phoneNumberError.value = context.getString(R.string.error_valid_phone)
                return false
            }

            else -> phoneNumberError.value = ""
        }
        return true

    }


    fun validateFullName(context: Context) : Boolean {
        when {
            fullName.value.isNullOrEmpty() -> {
                fullNameError.value = context.getString(R.string.error_empty_name)
                return false
            }

            else -> fullNameError.value = ""
        }
        return true

    }

    fun validateEmailPassword(context: Context) : Boolean {
        return validateEmail(context) && validatePassword(context)
    }

    fun validateContactDetails(context: Context) : Boolean {
        return validateFullName(context) && validatePhoneNumber(context)
    }

    private fun validateSignUp(context: Context) : Boolean {
        return validateEmail(context) && validatePassword(context) && validateFullName(context) && validatePhoneNumber(context)
    }

    fun signUpUser(context: Context) : MutableLiveData<GenericResponse>? {

        if(!validateSignUp(context)) return null

        setOtherAddressValues()
        val signUpData = SignupData(email = email.value ?: "",password = password.value ?: "",fullName = fullName.value ?: "",
        phoneNumber = phoneNumber.value ?: "",homeAddress = homeAddress.value)


        return SignUpRepository<User>(signUpData,context).getResponse()
    }

    private fun setOtherAddressValues() {
        homeAddress.value?.houseNumber = houseNumber.value
        homeAddress.value?.floorNumber = floorNumber.value
        homeAddress.value?.landmark = landmark.value
    }

}