package com.foodsgully.foodsgullyuser.adapters.viewholders

import android.net.Uri
import androidx.databinding.library.baseAdapters.BR
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.LayoutCardCartItemBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils

class CartViewHolder(private val viewBinding : LayoutCardCartItemBinding, private val variables : HashMap<Int,Any?>) : BaseViewHolder<CartItem>(viewBinding),
    FileDownloadResponseHandler {

    override fun bind(item: CartItem, position: Int) {

        variables[BR.cartItem] = item
        setVariables(variables)
        FirebaseFileUploadService(viewBinding.root.context).downloadFile(item.selectedProduct.imageUrl ?: "",3,this,
            FileType.PRODUCT_IMAGE)
        initializeUpdateItemListeners(item = item)
    }

    private fun initializeUpdateItemListeners(item: CartItem) {

        viewBinding.btnAddQuantity.setOnClickListener { addProductQuantityInCart(item) }
        viewBinding.btnRemoveQuantity.setOnClickListener { decreaseQuantityAndUpdateCart(item) }
        viewBinding.btnRemove.setOnClickListener { removeItemFromCart(item) }
    }

    private fun removeItemFromCart(item: CartItem) {

        viewBinding.updateProductListener?.updateProduct(item.selectedProduct,0)
    }

    private fun decreaseQuantityAndUpdateCart(item: CartItem) {
        val currentQuantity = getCurrentQuantity() - 1
        item.totalQuantity = currentQuantity
        item.itemPrice = currentQuantity * (item.selectedProduct.salePrice?.toDouble() ?: 0.0)
        viewBinding.updateProductListener?.updateProduct(item.selectedProduct,currentQuantity)
        updateQuantityInView(currentQuantity)

    }



    private fun addProductQuantityInCart(item: CartItem) {
        val currentQuantity = getCurrentQuantity() + 1
        item.totalQuantity = currentQuantity
        item.itemPrice = currentQuantity * (item.selectedProduct.salePrice?.toDouble() ?: 0.0)

        viewBinding.updateProductListener?.updateProduct(item.selectedProduct,currentQuantity)
        updateQuantityInView(currentQuantity)
    }


    private fun getCurrentQuantity() : Int = viewBinding.tvProductQuantity.text.toString().toInt()

    private fun updateQuantityInView(quantity: Int) {
        viewBinding.tvProductQuantity.text = "$quantity"
    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        FoodsGullyUtils.setImageToView(viewBinding.ivProductImage,fileUri,viewBinding.root.context,
            R.drawable.product_sample_image)
    }

    override fun onFileDownloadFailed(fileType: FileType) {
        FoodsGullyUtils.setImageToView(viewBinding.ivProductImage, Uri.EMPTY,viewBinding.root.context,
            R.drawable.product_sample_image)
    }
}