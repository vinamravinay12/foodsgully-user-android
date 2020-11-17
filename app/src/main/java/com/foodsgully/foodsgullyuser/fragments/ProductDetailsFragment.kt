package com.foodsgully.foodsgullyuser.fragments

import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.ProductDetailsFragmentBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.utils.listeners.UpdateProductInCartListener
import com.foodsgully.foodsgullyuser.viewmodels.ProductDetailsViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.ProductDetailsViewModelFactory

class ProductDetailsFragment : Fragment(),FileDownloadResponseHandler,UpdateProductInCartListener {

    private lateinit var bindingProductDetailsFragment: ProductDetailsFragmentBinding
    private var productDetailsViewModel : ProductDetailsViewModel? = null
    private var selectedProduct : Product? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            selectedProduct = getParcelable(FoodsGullyConstants.ARG_PRODUCT) as? Product
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingProductDetailsFragment = DataBindingUtil.inflate(inflater,R.layout.product_details_fragment, container, false)
        FoodsGullyUtils.clearViewModel(requireActivity())
        bindingProductDetailsFragment.lifecycleOwner = viewLifecycleOwner
        return bindingProductDetailsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        productDetailsViewModel = ProductDetailsViewModelFactory(selectedProduct).getViewModel(true,requireActivity())

        initializeVariables()
        strikeMrpIfDiscountIsNotZero()
        setProductImage()
        initializeQuantity(selectedProduct)
        initializeListeners()
    }

    override fun onResume() {
        super.onResume()
        FoodsGullyUtils.hideKeyboard(bindingProductDetailsFragment.root,requireContext())
    }


    private fun initializeQuantity(item : Product?) {

        val cartItems = FoodsGullyUtils.getItemsInCart(requireContext())
        val index = cartItems.indexOfFirst { it.selectedProduct.productId.equals(item?.productId) }
        if(index >= 0 && cartItems[index].totalQuantity > 0 ) {
            showOrHideAddItemButton(cartItems[index].totalQuantity)
            updateQuantityInView(cartItems[index].totalQuantity)
        }
    }

    private fun initializeVariables() {
        bindingProductDetailsFragment.productDetailsVM = productDetailsViewModel
        bindingProductDetailsFragment.updateProductListener = this
    }


    private fun setProductImage() {
        FirebaseFileUploadService(bindingProductDetailsFragment.root.context).downloadFile(selectedProduct?.imageUrl ?: "",3,this,FileType.PRODUCT_IMAGE)
    }

    private fun strikeMrpIfDiscountIsNotZero() {
        if(productDetailsViewModel?.getDiscount()?.value ?: 0  > 0) {
            bindingProductDetailsFragment.tvMrp.paintFlags = bindingProductDetailsFragment.tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        FoodsGullyUtils.setImageToView(bindingProductDetailsFragment.ivProductImage,fileUri,requireContext(),
            R.drawable.product_sample_image)
    }

    override fun onFileDownloadFailed(fileType: FileType) {
        FoodsGullyUtils.setImageToView(bindingProductDetailsFragment.ivProductImage, Uri.EMPTY,bindingProductDetailsFragment.root.context,
            R.drawable.product_sample_image)
    }

    private fun initializeListeners() {

        selectedProduct?.let { product ->

            bindingProductDetailsFragment.btnAddToCart.setOnClickListener { addProductToCart(product)}
            bindingProductDetailsFragment.btnAddQuantity.setOnClickListener { addProductQuantityInCart(product) }
            bindingProductDetailsFragment.btnRemoveQuantity.setOnClickListener { decreaseQuantityAndUpdateCart(product) }
        }

        bindingProductDetailsFragment.ibBack.setOnClickListener { findNavController().popBackStack(R.id.nav_home,false) }

    }


    private fun decreaseQuantityAndUpdateCart(product: Product) {
        if(getCurrentQuantity() > 0) {
            val currentQuantity = getCurrentQuantity() - 1

            bindingProductDetailsFragment.updateProductListener?.updateProduct(product, currentQuantity)
            showOrHideAddItemButton(currentQuantity)
            updateQuantityInView(currentQuantity)
        }
    }

    private fun showOrHideAddItemButton(currentQuantity: Int) {
        bindingProductDetailsFragment.btnAddToCart.visibility = if(currentQuantity == 0) View.VISIBLE else View.GONE
        bindingProductDetailsFragment.layoutAddQuantity.visibility = if(currentQuantity == 0) View.GONE else View.VISIBLE
    }

    private fun addProductQuantityInCart(product: Product) {
        val currentQuantity = getCurrentQuantity() + 1
        bindingProductDetailsFragment.updateProductListener?.updateProduct(product,currentQuantity)
        updateQuantityInView(currentQuantity)
    }

    private fun addProductToCart(product: Product) {
        bindingProductDetailsFragment.updateProductListener?.updateProduct(product,1)
        showOrHideAddItemButton(1)
        updateQuantityInView(1)
    }

    private fun getCurrentQuantity() : Int = bindingProductDetailsFragment.tvProductQuantity.text.toString().toInt()

    private fun updateQuantityInView(quantity: Int) {
        bindingProductDetailsFragment.tvProductQuantity.text = "$quantity"
    }

    override fun updateProduct(product: Product, quantity: Int) {

        var cartItems = FoodsGullyUtils.getItemsInCart(requireContext())
        if (cartItems.isEmpty() || cartItems.indexOfFirst {
                it.selectedProduct.productId.equals(
                    product.productId
                )
            } < 0) {
            cartItems.add(
                CartItem(
                    product,
                    quantity,
                    (quantity * (product.salePrice?.toDouble() ?: 0.0))
                )
            )
            FoodsGullyUtils.saveItemInCart(requireContext(), cartItems)
            updateCartView(cartItems)
            return
        }

        val existingCartItem =
            cartItems.first { it.selectedProduct.productId.equals(product.productId) }
        if (quantity == 0) cartItems.remove(existingCartItem)
        else {
            existingCartItem.totalQuantity = quantity
            existingCartItem.itemPrice = (product.salePrice?.toDouble() ?: 0.0) * quantity
        }

        FoodsGullyUtils.saveItemInCart(requireContext(), cartItems)
        updateCartView(cartItems)
    }


    private fun updateCartView(cartItems: ArrayList<CartItem>) {

        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(cartItems)
            mainActivity.updateCartBadge(cartItems.size)

        }

    }

}