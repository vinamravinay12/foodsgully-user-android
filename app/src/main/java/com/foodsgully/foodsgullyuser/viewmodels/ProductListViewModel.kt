package com.foodsgully.foodsgullyuser.viewmodels


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.ProductTypesAdapter
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.ProductType
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.listeners.UpdateProductInCartListener
import com.foodsgully.foodsgullyuser.viewmodels.repositories.FetchProductsRepository
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProductListViewModel : ListViewModel<ProductType>() {

    private val products = MutableLiveData<List<Product>>()
    private val productTypes = MediatorLiveData<MutableList<ProductType>>()
    private val productTypeList = LinkedList<ProductType>()


    private lateinit var productTypeAdapter : ProductTypesAdapter

    fun initializeAdapters(viewStoreOwner : ViewModelStoreOwner,productVHVariables: HashMap<Int,Any?>) {
        productTypeAdapter = ProductTypesAdapter(productTypes.value ?: ArrayList<ProductType>().toMutableList(),getViewType(),viewStoreOwner,productVHVariables)
    }


    override fun getViewType(): Int {
        return R.layout.layout_product_header
    }

    override fun updateList() {
        productTypeAdapter.updateList(productTypeList.toMutableList())
    }


    fun getProducts() = products


    fun setProductTypeList(categoryImageBitmap : Bitmap?) {
        productTypeList.clear()
        val iterator = products.value?.iterator()
        while(iterator != null && iterator.hasNext()) {
            val item = iterator.next()
            item.categoryImageBitmap = categoryImageBitmap
            val index = productTypeList.indexOfFirst { it.type.trim().equals(item.type?.trim(),true) }
            if( index >= 0) {
                (productTypeList[index].products as ArrayList).add(item)

            } else {

                productTypeList.add(ProductType(item.type ?: "", arrayListOf(item)))
            }

        }
        updateList()

    }

    fun getProductTypeList() = productTypeList

    override fun getAdapter(): BaseRecyclerAdapter<ProductType>  = productTypeAdapter


    fun fetchProducts(categoryName : String,context: Context) : MutableLiveData<GenericResponse> {

        return FetchProductsRepository<List<Product>>(categoryName = categoryName,context = context).getResponse()
    }
}