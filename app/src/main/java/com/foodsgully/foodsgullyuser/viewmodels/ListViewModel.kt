package com.foodsgully.foodsgullyuser.viewmodels

import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter

abstract class ListViewModel<T> : ViewModel() {

    abstract fun getViewType(): Int
    abstract fun updateList()
    abstract fun getAdapter() : BaseRecyclerAdapter<T>

}