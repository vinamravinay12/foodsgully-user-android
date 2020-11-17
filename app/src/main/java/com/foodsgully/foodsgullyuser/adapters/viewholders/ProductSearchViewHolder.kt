package com.foodsgully.foodsgullyuser.adapters.viewholders

import android.net.Uri
import androidx.databinding.library.baseAdapters.BR
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.CardCategoryBinding
import com.foodsgully.foodsgullyuser.databinding.CardProductSearchBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils

class ProductSearchViewHolder(private val viewBinding : CardProductSearchBinding, private val variables : HashMap<Int,Any?>) : BaseViewHolder<Product>(viewBinding){

    override fun bind(item: Product, position: Int) {
        variables[BR.product] = item
        setVariables(variables)

        viewBinding.tvProductName.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }
    }


}