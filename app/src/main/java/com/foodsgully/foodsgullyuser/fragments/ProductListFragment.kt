package com.foodsgully.foodsgullyuser.fragments

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.ProductListFragmentBinding
import com.foodsgully.foodsgullyuser.firebase.FileDownloadResponseHandler
import com.foodsgully.foodsgullyuser.firebase.FileType
import com.foodsgully.foodsgullyuser.firebase.FirebaseFileUploadService
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.utils.listeners.ItemClickListener
import com.foodsgully.foodsgullyuser.utils.listeners.QueryTextChangeListener
import com.foodsgully.foodsgullyuser.utils.listeners.SearchQueryListener
import com.foodsgully.foodsgullyuser.utils.listeners.UpdateProductInCartListener
import com.foodsgully.foodsgullyuser.viewmodels.ProductListViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ProductSearchViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.ProductListVMFactory
import com.foodsgully.foodsgullyuser.viewmodels.factories.ProductSearchViewModelFactory


class ProductListFragment : Fragment(),
    UpdateProductInCartListener, FileDownloadResponseHandler, ItemClickListener,
    QueryTextChangeListener {

    private lateinit var bindingLayoutProductHeaderBinding: ProductListFragmentBinding
    private var productListViewModel: ProductListViewModel? = null
    private var productSearchViewModel: ProductSearchViewModel? = null
    private var selectedCategory: Category? = null
    private var mProgressDialog: AlertDialog? = null
    private var mSearchQuery = ""

    companion object {
        fun newInstance() = ProductListFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            selectedCategory = getParcelable(FoodsGullyConstants.ARG_CATEGORY) as? Category
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingLayoutProductHeaderBinding =
            DataBindingUtil.inflate(inflater, R.layout.product_list_fragment, container, false)
        bindingLayoutProductHeaderBinding.lifecycleOwner = viewLifecycleOwner
        return bindingLayoutProductHeaderBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        productListViewModel = ProductListVMFactory().getViewModel(
            hasArgs = false,
            owner = requireActivity()
        )

        productSearchViewModel =
            ProductSearchViewModelFactory().getViewModel(false, requireActivity())


        initializeProductsView()
        initializeCategoryDetails()
        setupSearchView()
        initializeProductSearchView()
        initializeListeners()
        fetchProducts()

    }

    private fun setupSearchView() {
        val searchManager: SearchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchIcon: ImageView =
            bindingLayoutProductHeaderBinding.searchProducts.findViewById(androidx.appcompat.R.id.search_button)

        val editText : EditText = bindingLayoutProductHeaderBinding.searchProducts.findViewById(androidx.appcompat.R.id.search_src_text)

        editText.setTextColor(Color.WHITE)
        searchIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)

    }

    private fun initializeProductSearchView() {

        bindingLayoutProductHeaderBinding.rvSearchProductsResult.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = productSearchViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())

        bindingLayoutProductHeaderBinding.rvSearchProductsResult.setHasFixedSize(true)
        bindingLayoutProductHeaderBinding.rvSearchProductsResult.adapter = adapter
    }

    private fun getVariables(): HashMap<Int, Any?> {
        return hashMapOf(BR.itemClickListener to this, BR.updateProductListener to this)
    }

    private fun initializeListeners() {
        bindingLayoutProductHeaderBinding.ivBack.setOnClickListener { findNavController().popBackStack(R.id.nav_home,false) }
        bindingLayoutProductHeaderBinding.searchProducts.setOnQueryTextListener(SearchQueryListener(this))
    }


    private fun fetchProducts() {

        productListViewModel?.fetchProducts(selectedCategory?.categoryName ?: "", requireContext())
            ?.observe(viewLifecycleOwner,
                Observer { response ->
                    when (response) {

                        is APILoader -> {
                            mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                                getString(R.string.fetching_products),
                                requireContext()
                            )
                        }

                        is APIError -> {
                            FoodsGullyUtils.dismissProgress(mProgressDialog)
                            FoodsGullyUtils.showSnackbar(
                                response.errorMessage,
                                bindingLayoutProductHeaderBinding.root
                            )
                        }

                        is Success<*> -> {
                            FoodsGullyUtils.dismissProgress(mProgressDialog)

                            productListViewModel?.getProducts()?.value =
                                ((response.data as? List<Product>)?.toMutableList() ?: ArrayList())
                            productListViewModel?.setProductTypeList(selectedCategory?.categoryImageBitmap)
                            initializeTotalItems()
                        }
                    }
                })

    }

    private fun initializeTotalItems() {
        val totalProducts = productListViewModel?.getProducts()?.value?.size ?: 0
        val suffix =
            if (totalProducts < 1) getString(R.string.txt_item) else getString(R.string.txt_items)

        bindingLayoutProductHeaderBinding.tvTotalItems.text =
            StringBuilder().append(totalProducts).append(" ").append(suffix)

    }

    private fun initializeCategoryDetails() {
        bindingLayoutProductHeaderBinding.tvCategoryName.text = selectedCategory?.categoryName ?: ""

        if(selectedCategory?.categoryImageBitmap != null) bindingLayoutProductHeaderBinding.ivCategoryImage.setImageBitmap(selectedCategory?.categoryImageBitmap)

        else {

            FirebaseFileUploadService(requireContext()).downloadFile(
                selectedCategory?.categoryUrl ?: "", 3, this, FileType.CATEGORY_IMAGE
            )

        }
        bindingLayoutProductHeaderBinding.layoutCategoryHeader.setBackgroundColor(
            Color.parseColor(
                selectedCategory?.categoryColor ?: ""
            )
        )
        val (red, green, blue) = FoodsGullyUtils.getRGBFromHex(selectedCategory?.categoryColor)

        bindingLayoutProductHeaderBinding.layoutSearch.setBackgroundColor(
            Color.argb(
                (255 * 0.32).toInt(),
                red,
                green,
                blue
            )
        )

        bindingLayoutProductHeaderBinding.productCollapsingHeader.setContentScrimColor( Color.parseColor(
            selectedCategory?.categoryColor ?: ""
        ))
    }

    private fun initializeProductsView() {
        bindingLayoutProductHeaderBinding.rvProducts.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        productListViewModel?.initializeAdapters(requireActivity(), getVariables())
        val adapter = productListViewModel?.getAdapter()
        bindingLayoutProductHeaderBinding.rvProducts.setHasFixedSize(true)
        bindingLayoutProductHeaderBinding.rvProducts.adapter = adapter
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

    override fun onFileDownloaded(fileUri: Uri, fileType: FileType) {
        FoodsGullyUtils.setImageToView(
            bindingLayoutProductHeaderBinding.ivCategoryImage,
            fileUri,
            requireContext(),
            R.drawable.category_sample_image
        )

    }

    override fun onFileDownloadFailed(fileType: FileType) {
        FoodsGullyUtils.setImageToView(
            bindingLayoutProductHeaderBinding.ivCategoryImage,
            Uri.EMPTY,
            requireContext(),
            R.drawable.category_sample_image
        )
    }

    override fun onItemClick(item: Any?) {

        findNavController().navigate(R.id.nav_product_details, bundleOf(FoodsGullyConstants.ARG_PRODUCT to item as? Product))
    }

    override fun onQueryTextChanged(query: String?) {
        if (query.isNullOrEmpty()) {
            mSearchQuery = ""
            FoodsGullyUtils.hideKeyboard(bindingLayoutProductHeaderBinding.root,requireContext())
            productSearchViewModel?.setProducts(ArrayList<Product>())
            bindingLayoutProductHeaderBinding.rvSearchProductsResult.visibility = View.INVISIBLE
            return
        }

        mSearchQuery = query


        productSearchViewModel?.searchProducts(query, requireContext())?.observe(viewLifecycleOwner,
            Observer { response ->

                when (response) {

                    is APILoader -> {
                        // mProgressDialog =  FoodsGullyUtils.showLoaderProgress("",requireContext())
                    }

                    is APIError -> {
                        //  FoodsGullyUtils.dismissProgress(mProgressDialog)
                        //   FoodsGullyUtils.showSnackbar(response.errorMessage,bindingHomeFragment.root)
                    }

                    is Success<*> -> {
                        //  FoodsGullyUtils.dismissProgress(mProgressDialog)

                        if(mSearchQuery.isNotEmpty()) {
                            bindingLayoutProductHeaderBinding.rvSearchProductsResult.visibility = View.VISIBLE

                            productSearchViewModel?.setProducts(response.data as? List<Product>)
                        }
                    }
                }

            })
    }

}

class CustomLinearLayoutManager(context: Context, orientation : Int, reversible : Boolean ) : LinearLayoutManager(context, orientation,reversible) {

    override fun canScrollVertically(): Boolean {
        return false
    }
}