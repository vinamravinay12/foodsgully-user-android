package com.foodsgully.foodsgullyuser.adapters.viewholders

import android.net.Uri
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.LayoutCardProductBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils

class ProductViewHolder(private val viewBinding : LayoutCardProductBinding, private val variables : HashMap<Int,Any?>) : BaseViewHolder<Product>(viewBinding),
    FileDownloadResponseHandler {


    override fun bind(item: Product, position: Int) {

        variables[BR.product] = item
        setVariables(variables)
        if(item.categoryImageBitmap != null)viewBinding.ivProductImage.setImageBitmap(item.categoryImageBitmap)
        FirebaseFileUploadService(viewBinding.root.context).downloadFile(item.imageUrl ?: "",3,this,FileType.PRODUCT_IMAGE)
        initializeListeners(item)

        initializeQuantity(item)
    }

    private fun initializeListeners(item: Product) {
        viewBinding.root.setOnClickListener { viewBinding.itemClickListener?.onItemClick(item) }
        initializeUpdateItemListeners(product = item)
    }

    private fun initializeQuantity(item : Product) {

        val cartItems = FoodsGullyUtils.getItemsInCart(viewBinding.root.context)
        val index = cartItems.indexOfFirst { it.selectedProduct.productId.equals(item.productId) }
        if(index >= 0 && cartItems[index].totalQuantity > 0 ) {
            showOrHideAddItemButton(cartItems[index].totalQuantity)
            updateQuantityInView(cartItems[index].totalQuantity)
        }
    }

    private fun initializeUpdateItemListeners(product: Product) {
        viewBinding.btnAdd.setOnClickListener {  addProductToCart(product) }
        viewBinding.btnAddQuantity.setOnClickListener { addProductQuantityInCart(product) }
        viewBinding.btnRemoveQuantity.setOnClickListener { decreaseQuantityAndUpdateCart(product) }
    }

    private fun decreaseQuantityAndUpdateCart(product: Product) {
        if(getCurrentQuantity() > 0) {
            val currentQuantity = getCurrentQuantity() - 1

            viewBinding.updateProductListener?.updateProduct(product, currentQuantity)
            showOrHideAddItemButton(currentQuantity)
            updateQuantityInView(currentQuantity)
        }
    }

    private fun showOrHideAddItemButton(currentQuantity: Int) {
            viewBinding.btnAdd.visibility = if(currentQuantity == 0) View.VISIBLE else View.GONE
            viewBinding.layoutAddQuantity.visibility = if(currentQuantity == 0) View.GONE else View.VISIBLE
    }

    private fun addProductQuantityInCart(product: Product) {
        val currentQuantity = getCurrentQuantity() + 1
        viewBinding.updateProductListener?.updateProduct(product,currentQuantity)
        updateQuantityInView(currentQuantity)
    }

    private fun addProductToCart(product: Product) {
        viewBinding.updateProductListener?.updateProduct(product,1)
        showOrHideAddItemButton(1)
        updateQuantityInView(1)
    }

    private fun getCurrentQuantity() : Int = viewBinding.tvProductQuantity.text.toString().toInt()

    private fun updateQuantityInView(quantity: Int) {
        viewBinding.tvProductQuantity.text = "$quantity"
    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        FoodsGullyUtils.setImageToView(viewBinding.ivProductImage,fileUri,viewBinding.root.context,
            R.drawable.category_sample_image)
    }

    override fun onFileDownloadFailed(fileType: FileType) {
        FoodsGullyUtils.setImageToView(viewBinding.ivProductImage, Uri.EMPTY,viewBinding.root.context,
            R.drawable.category_sample_image)
    }
}