package com.foodsgully.foodsgullyuser.viewmodels


import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.CategoryAdapter
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.viewmodels.repositories.CategoriesRepository


class HomeViewModel : ListViewModel<Category>() {


    private val categories = ArrayList<Category>().toMutableList()

    private lateinit var categoryAdapter : CategoryAdapter


    init {
        categoryAdapter = CategoryAdapter(categories,getViewType())
    }

    fun getCategories() = categories


    override fun getViewType(): Int {

        return R.layout.card_category
    }

    override fun updateList() {

        categoryAdapter.updateList(categories.toMutableList())

    }

    override fun getAdapter(): BaseRecyclerAdapter<Category>  = categoryAdapter

    fun fetchCategories(context: Context) : MutableLiveData<GenericResponse> = CategoriesRepository<List<Category>>(context).getResponse()
    

}