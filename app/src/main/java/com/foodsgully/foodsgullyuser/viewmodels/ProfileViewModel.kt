package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdateProfileData
import com.foodsgully.foodsgullyuser.models.responsemodels.GenericAPIResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.viewmodels.repositories.UpdateProfileRepository
import com.wajahatkarim3.easyvalidation.core.view_ktx.validNumber

class ProfileViewModel : ViewModel() {


    private val userData = MutableLiveData<User>()
    private var fullNameError = MutableLiveData<String>()
    private var phoneNumberError = MutableLiveData<String>()
    private val fullName = MutableLiveData<String>()
    private val phoneNumber = MutableLiveData<String>()
    private val imageUrl = MutableLiveData<String>()
    private var imagePath = MutableLiveData<Uri>()
    private var imageName = MutableLiveData<String>()

    fun setUserData(user : User?) {
        userData.value = user
        initializeUserData()
    }

    fun getUser() = userData.value

    fun getImagePath() = imagePath
    fun getImageName() = imageName

    fun getPhoneNumberError() = phoneNumberError
    fun getFullNameError() = fullNameError

    fun getFullName() = fullName

    fun getEmail() = MutableLiveData<String>().apply {
        this.value = userData.value?.email ?: ""
    }

    fun getPhoneNumber() = phoneNumber

    fun getImageUrl() = imageUrl

    fun validatePhoneNumber(context: Context) : Boolean {
        when {
            userData.value?.phoneNumber.isNullOrEmpty() -> {
                phoneNumberError.value = context.getString(R.string.error_empty_phonenumber)
                return false
            }

            userData.value?.phoneNumber?.validNumber() == false && userData.value?.phoneNumber?.length != 10 -> {
                phoneNumberError.value = context.getString(R.string.error_valid_phone)
            }

            else -> phoneNumberError.value = ""
        }
        return true

    }


    fun validateFullName(context: Context) : Boolean {
        when {
            userData.value?.fullName.isNullOrEmpty() -> {
                fullNameError.value = context.getString(R.string.error_empty_name)
                return false
            }

            else -> fullNameError.value = ""
        }
        return true

    }

    private fun validateContactDetails(context: Context) : Boolean {
        return validateFullName(context) && validatePhoneNumber(context)
    }


    fun updateProfileDetails(context: Context) : MutableLiveData<GenericResponse>? {

        if(!validateContactDetails(context)) return null

        updateUserData()

        return UpdateProfileRepository<String>(UpdateProfileData(fullName = userData.value?.fullName,phoneNumber = userData.value?.phoneNumber,imageUrl = imageUrl.value),context).getResponse()
    }

    private fun updateUserData() {
        userData.value?.fullName = fullName.value
        userData.value?.phoneNumber = phoneNumber.value
        userData.value?.imageUrl = imageUrl.value
    }


    private fun initializeUserData() {
        fullName.value = userData.value?.fullName
        phoneNumber.value = userData.value?.phoneNumber
        imageUrl.value = userData.value?.imageUrl
    }




}