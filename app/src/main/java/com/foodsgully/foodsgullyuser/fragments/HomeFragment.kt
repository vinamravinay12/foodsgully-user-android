package com.foodsgully.foodsgullyuser.fragments

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.activities.MainActivity
import com.foodsgully.foodsgullyuser.databinding.HomeFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.Category
import com.foodsgully.foodsgullyuser.models.responsemodels.Product
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.utils.listeners.ImageBitmapDownloadedListener
import com.foodsgully.foodsgullyuser.utils.listeners.ItemClickListener
import com.foodsgully.foodsgullyuser.utils.listeners.QueryTextChangeListener
import com.foodsgully.foodsgullyuser.utils.listeners.SearchQueryListener
import com.foodsgully.foodsgullyuser.viewmodels.HomeViewModel
import com.foodsgully.foodsgullyuser.viewmodels.ProductSearchViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.HomeViewModelFactory
import com.foodsgully.foodsgullyuser.viewmodels.factories.ProductSearchViewModelFactory

import com.synnapps.carouselview.ImageListener

class HomeFragment : Fragment(),
    ItemClickListener, ImageListener, QueryTextChangeListener,ImageBitmapDownloadedListener {

    private lateinit var bindingHomeFragment: HomeFragmentBinding
    private var homeViewModel: HomeViewModel? = null
    private var productSearchViewModel: ProductSearchViewModel? = null
    private var mProgressDialog: AlertDialog? = null
    private var mPreviousTimeForKeyStroke: Long = -1
    private var mSearchQuery = ""

    private var carouselImages = arrayOf(
        R.drawable.carousel_image_1,
        R.drawable.carousel_image_1,
        R.drawable.carousel_image_1
    )

    companion object {
        fun newInstance() = HomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingHomeFragment =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        bindingHomeFragment.lifecycleOwner = viewLifecycleOwner
        return bindingHomeFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel = HomeViewModelFactory().getViewModel(false, requireActivity())

        productSearchViewModel =
            ProductSearchViewModelFactory().getViewModel(false, requireActivity())

        initializeCategoriesView()
        setupSearchView()
        initializeProductSearchView()
        initializeListeners()

    }


    override fun onStart() {
        super.onStart()
        showToolbar(true)
    }


    override fun onStop() {
        super.onStop()
        showToolbar(false)
    }

    override fun onResume() {
        super.onResume()
        initializeCarouselView()
        fetchCategories()
        showProceedToCartLayout()

    }

    private fun setupSearchView() {
        val searchManager: SearchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager


    }

    private fun showProceedToCartLayout() {
        val cartItems = FoodsGullyUtils.getItemsInCart(requireContext())
        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showOrHideCartLayout(cartItems)
        }
    }


    private fun showToolbar(toShow: Boolean) {
        if (requireActivity() is MainActivity) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.hideToolbar(!toShow)
        }
    }

    private fun initializeCategoriesView() {
        bindingHomeFragment.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = homeViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingHomeFragment.rvCategories.setHasFixedSize(true)
        bindingHomeFragment.rvCategories.adapter = adapter
    }


    private fun initializeProductSearchView() {

        bindingHomeFragment.rvSearchProductsResult.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = productSearchViewModel?.getAdapter()
        adapter?.setVariablesMap(getVariables())
        bindingHomeFragment.rvSearchProductsResult.setHasFixedSize(true)
        bindingHomeFragment.rvSearchProductsResult.adapter = adapter
    }

    private fun getVariables(): HashMap<Int, Any?> {
        return hashMapOf(BR.itemClickListener to this)
    }

    private fun initializeCarouselView() {
        FoodsGullyUtils.initializeAndSetCarouselImages(
            bindingHomeFragment.carouselView,
            carouselImages,
            this
        )

    }


    private fun fetchCategories() {

        homeViewModel?.fetchCategories(requireContext())
            ?.observe(viewLifecycleOwner, Observer { response ->
                when (response) {

                    is APILoader -> {
                        mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                            getString(R.string.fetching_categories),
                            requireContext()
                        )
                    }

                    is APIError -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        FoodsGullyUtils.showSnackbar(
                            response.errorMessage,
                            bindingHomeFragment.root
                        )
                    }

                    is Success<*> -> {
                        FoodsGullyUtils.dismissProgress(mProgressDialog)
                        updateCategoriesList(response.data as? List<Category>)
                    }
                }
            })
    }

    private fun updateCategoriesList(categories: List<Category>?) {
        homeViewModel?.getCategories()?.clear()
        homeViewModel?.getCategories()?.addAll(categories?.toMutableList() ?: ArrayList())
        homeViewModel?.updateList()
    }

    private fun initializeListeners() {
        bindingHomeFragment.etSearch.setOnQueryTextListener(SearchQueryListener(this))

    }

    override fun onItemClick(item: Any?) {

        when (item) {
            is Category -> {
                val selectedCategory = item as? Category
                goToProductsScreen(selectedCategory)
            }
            is Product -> {
                goToProductDetailsScreen(item as? Product)
            }
        }


    }

    private fun goToProductDetailsScreen(product: Product?) {
        findNavController().navigate(
            R.id.nav_product_details,
            bundleOf(FoodsGullyConstants.ARG_PRODUCT to product)
        )
    }

    private fun goToProductsScreen(selectedCategory: Category?) {
        findNavController().navigate(
            R.id.nav_products,
            bundleOf(FoodsGullyConstants.ARG_CATEGORY to selectedCategory)
        )
    }

    override fun setImageForPosition(position: Int, imageView: ImageView?) {
        if (carouselImages.isEmpty()) return
        imageView?.setImageResource(carouselImages[position])
    }

    override fun onQueryTextChanged(query: String?) {

        if (query.isNullOrEmpty()) {
            mPreviousTimeForKeyStroke = -1
            mSearchQuery = ""
            productSearchViewModel?.setProducts(ArrayList<Product>())
            bindingHomeFragment.rvSearchProductsResult.visibility = View.INVISIBLE
            return
        }

        mSearchQuery = query

        if (mPreviousTimeForKeyStroke == -1L) {
            mPreviousTimeForKeyStroke = System.currentTimeMillis()
        }

        Log.i("TAG", "time diff " + (System.currentTimeMillis() - mPreviousTimeForKeyStroke))
        mPreviousTimeForKeyStroke = System.currentTimeMillis()
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

                        if (mSearchQuery.isNotEmpty()) {
                            bindingHomeFragment.rvSearchProductsResult.visibility = View.VISIBLE

                            productSearchViewModel?.setProducts(response.data as? List<Product>)
                        }
                    }
                }

            })

    }

    override fun onBitmapReady(bitmap: Bitmap, uri: Uri) {

    }


}


