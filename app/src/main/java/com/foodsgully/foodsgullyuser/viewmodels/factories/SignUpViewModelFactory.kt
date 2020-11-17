package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.SignUpViewModel


class SignUpViewModelFactory() : AbstractViewModelFactory<SignUpViewModel>() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return SignUpViewModel() as T
    }
}