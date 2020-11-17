package com.foodsgully.foodsgullyuser.viewmodels.factories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

abstract class AbstractViewModelFactory<T : ViewModel>() : ViewModelProvider.Factory {

    inline fun <reified T: ViewModel> getViewModel( hasArgs : Boolean, owner: ViewModelStoreOwner) : T {
        if(!hasArgs) {
            return ViewModelProvider(owner).get(T::class.java)
        }

        return ViewModelProvider(owner,this).get(T::class.java)

    }
}