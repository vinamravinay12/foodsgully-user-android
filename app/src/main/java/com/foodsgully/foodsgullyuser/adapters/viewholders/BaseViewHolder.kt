package com.foodsgully.foodsgullyuser.adapters.viewholders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder<T>(private val viewBinding : ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    abstract fun bind(item: T, position : Int)

    fun setVariables(variablesMap : HashMap<Int,Any?>) {
        if(variablesMap.size == 0) return
        for(key in variablesMap.keys) {
            viewBinding.setVariable(key,variablesMap[key])
        }
    }

}