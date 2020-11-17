package com.foodsgully.foodsgullyuser.adapters

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder

abstract class BaseRecyclerAdapter<T>(private var dataList: MutableList<T>?) : RecyclerView.Adapter<BaseViewHolder<T>>(){

    private val completeList = ArrayList<T>()

    init {

        if(dataList == null)dataList = ArrayList()
        completeList.clear()
        dataList?.let { completeList.addAll(it) }

    }

    private var variablesMap =  HashMap<Int,Any?>()



    fun getVariablesMap() = variablesMap

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder<T>

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val item = dataList?.get(position)
        item?.let { holder.bind(it,position) }
    }

    fun setVariablesMap(variablesMap : HashMap<Int,Any?> ) {
        this.variablesMap = variablesMap
    }



    override fun getItemCount(): Int {
        return dataList?.count() ?: 0
    }



    open fun updateList(newList : MutableList<T>?) {

        dataList = newList
        completeList.clear()
        newList?.let { completeList.addAll(it) }


        notifyDataSetChanged()

    }

    fun updateFilterList(newList: MutableList<T>?) {
        dataList?.clear()
        newList?.let {
            dataList?.addAll(it)

        }

        notifyDataSetChanged()
    }




    fun getItem(position: Int) : T? {
        return dataList?.get(position)
    }

    fun clear() {
        dataList?.clear()
        notifyDataSetChanged()
    }


//    override fun getFilter(): Filter {
//
//        return ResultsFilter(completeList.toMutableList(),filterResultListener)
//    }

}