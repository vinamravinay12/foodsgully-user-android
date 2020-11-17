package com.foodsgully.foodsgullyuser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelStoreOwner
import com.foodsgully.foodsgullyuser.adapters.viewholders.BaseViewHolder
import com.foodsgully.foodsgullyuser.adapters.viewholders.ProductHeaderViewHolder
import com.foodsgully.foodsgullyuser.databinding.LayoutProductHeaderBinding
import com.foodsgully.foodsgullyuser.models.ProductType
import com.foodsgully.foodsgullyuser.utils.listeners.UpdateProductInCartListener

class ProductTypesAdapter(private val dataList : MutableList<ProductType>, private val layoutRes : Int,private val viewModelStoreOwner: ViewModelStoreOwner,private val productVHVariables : HashMap<Int,Any?>) : BaseRecyclerAdapter<ProductType>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ProductType> {
        val inflater = LayoutInflater.from(parent.context)
        val bindingProductTypes = DataBindingUtil.inflate<LayoutProductHeaderBinding>(inflater,layoutRes,parent,false)
        return ProductHeaderViewHolder(bindingProductTypes,getVariablesMap(), viewModelStoreOwner,productVHVariables)
    }


}