package com.foodsgully.foodsgullyuser.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.CartFragmentBinding
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.utils.listeners.UpdateProductInCartListener
import com.foodsgully.foodsgullyuser.viewmodels.CartViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.CartViewModelFactory
import kotlin.collections.ArrayList


class CartFragment : Fragment(),
    UpdateProductInCartListener {

    private lateinit var bindingCartFragment : CartFragmentBinding
    private var cartViewModel : CartViewModel? = null


    companion object {
        fun newInstance() = CartFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCartFragment = DataBindingUtil.inflate(inflater,R.layout.cart_fragment, container, false)
        bindingCartFragment.lifecycleOwner = viewLifecycleOwner

        return bindingCartFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cartViewModel = CartViewModelFactory().getViewModel(false,requireActivity())

        bindingCartFragment.cartVM = cartViewModel


        initializeCartItemView()
        initializeCartItemList()
        setCartViewHeader()
        initializeListeners()

    }



    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(false)

        }

    }




    private fun initializeCartItemList() {
        val cartItems = FoodsGullyUtils.getItemsInCart(requireContext())
        cartViewModel?.getCartItems()?.clear()
        cartViewModel?.getCartItems()?.addAll(cartItems)
    }

    private fun initializeCartItemView() {
        bindingCartFragment.rvCartItems.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val adapter = cartViewModel?.getAdapter()
        adapter?.setVariablesMap(hashMapOf(BR.updateProductListener to this))
        bindingCartFragment.rvCartItems.setHasFixedSize(true)
        bindingCartFragment.rvCartItems.adapter = adapter
    }

    private fun setCartViewHeader() {
        val suffix = if(cartViewModel?.getTotalItems() ?: 0 > 1) getString(R.string.txt_items) else getString(R.string.txt_item)

        bindingCartFragment.tvCartHeader.text = String.format(getString(R.string.title_cart),cartViewModel?.getTotalItems() ?: 0, suffix)
    }



    private fun initializeListeners() {
        bindingCartFragment.btnCheckout.setOnClickListener { proceedToCheckout() }
        bindingCartFragment.ibBack.setOnClickListener {goToHomeScreen() }
    }

    private fun goToHomeScreen() {
        findNavController().popBackStack(R.id.nav_home,false)
    }

    private fun proceedToCheckout() {
        findNavController().navigate(R.id.nav_checkout, bundleOf(FoodsGullyConstants.ARG_FINAL_AMOUNT to cartViewModel?.getFinalCost()))
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
            return
        }

        val existingCartItem =
            cartItems.first { it.selectedProduct.productId.equals(product.productId) }

        if (quantity == 0){
            cartItems.remove(existingCartItem)
            cartViewModel?.removeItemFromList(existingCartItem)
        }
        else {
            existingCartItem.totalQuantity = quantity
            existingCartItem.itemPrice = (product.salePrice?.toDouble() ?: 0.0) * quantity
        }

        FoodsGullyUtils.saveItemInCart(requireContext(), cartItems)
        updateCartView(cartItems)
        setCartViewHeader()
        updateTotal(cartItems)

    }

    @SuppressLint("SetTextI18n")
    private fun updateTotal(cartItems: ArrayList<CartItem>) {
        val itemTotal = cartItems.map { it.itemPrice }.sum()

        bindingCartFragment.tvItemTotal.text = getString(R.string.rupee_symbol) + "" + itemTotal

        bindingCartFragment.tvGrandTotal.text = getString(R.string.rupee_symbol) + "" + itemTotal

    }

    private fun updateCartView(cartItems: ArrayList<CartItem>) {

        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(false)

        }

    }


}