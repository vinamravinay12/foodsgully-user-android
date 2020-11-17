package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.viewmodels.LoginViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ResetPasswordViewModel

class ResetPasswordVMFactory : AbstractViewModelFactory<ResetPasswordViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ResetPasswordViewModel() as T
    }
}