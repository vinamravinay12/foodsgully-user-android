package com.foodsgully.foodsgullyuser.adapters.viewholders

import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.ProductsAdapter
import com.foodsgully.foodsgullyuser.databinding.LayoutProductHeaderBinding
import com.foodsgully.foodsgullyuser.models.ProductType
import com.foodsgully.foodsgullyuser.utils.listeners.UpdateProductInCartListener

class ProductHeaderViewHolder(private val viewBinding : LayoutProductHeaderBinding,private val variables : HashMap<Int,Any?>,
                              private val viewStoreOwner : ViewModelStoreOwner,private val variablesMap : HashMap<Int,Any?>
) : BaseViewHolder<ProductType>(viewBinding) {




    override fun bind(item: ProductType, position: Int) {

        variables[BR.productType] = item
        setVariables(variables)

        initializeProductsRecyclerView(item)
        viewBinding.ivExpand.setOnClickListener { handleProductsExpandAndCollapse(item) }


    }


    private fun handleProductsExpandAndCollapse(productType: ProductType) {
        viewBinding.rvProducts.visibility = if(productType.isExpanded)  View.GONE else View.VISIBLE

        productType.isExpanded = !productType.isExpanded
        val iconDrawable = if(productType.isExpanded) R.drawable.ic_collapse_black_24 else R.drawable.ic_expand_black_24
        viewBinding.ivExpand.setImageResource(iconDrawable)
    }

    private fun initializeProductsRecyclerView(item : ProductType) {
        viewBinding.rvProducts.layoutManager = LinearLayoutManager(viewBinding.root.context,LinearLayoutManager.VERTICAL,false)

        val adapter = ProductsAdapter(item.products.toMutableList(),R.layout.layout_card_product)
        adapter.setVariablesMap(variablesMap)
        viewBinding.rvProducts.setHasFixedSize(true)
        viewBinding.rvProducts.adapter = adapter
    }

}


