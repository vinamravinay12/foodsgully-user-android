package com.foodsgully.foodsgullyuser.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.models.responsemodels.User
import com.foodsgully.foodsgullyuser.viewmodels.OrderDetailsViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ProfileViewModel

class ProfileViewModelFactory : AbstractViewModelFactory<ProfileViewModel>() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel() as T
    }
}