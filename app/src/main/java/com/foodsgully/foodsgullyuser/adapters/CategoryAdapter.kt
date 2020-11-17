package com.foodsgully.foodsgullyuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.CategoryViewHolder
import com.foodsgully.foodsgullyuser.databinding.CardCategoryBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.Category


class CategoryAdapter(private val dataList : MutableList<Category>,private val layoutRes : Int) : BaseRecyclerAdapter<Category>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Category> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingCategories = DataBindingUtil.inflate<CardCategoryBinding>(inflater,layoutRes,parent,false)
        return CategoryViewHolder(bindingCategories,getVariablesMap())
    }


}