package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.SignUpViewModel


class SignUpViewModelFactory(private val phoneNumber : String?) : AbstractViewModelFactory<SignUpViewModel>() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.getConstructor(String::class.java).newInstance(phoneNumber)
    }
}