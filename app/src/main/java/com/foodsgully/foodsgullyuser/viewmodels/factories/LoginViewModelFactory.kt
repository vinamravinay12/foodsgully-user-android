package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.LoginViewModel

class LoginViewModelFactory : AbstractViewModelFactory<LoginViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel() as T
    }
}