package com.foodsgully.foodsgullyuser.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.OrderHistoryFragmentBinding
import com.foodsgully.foodsgullyuser.models.APIError
import com.foodsgully.foodsgullyuser.models.APILoader
import com.foodsgully.foodsgullyuser.models.Success
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.utils.listeners.DateChangeListener
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.utils.listeners.ItemClickListener
import com.foodsgully.foodsgullyuser.viewmodels.OrderHistoryViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.OrderHistoryViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryFragment : Fragment(),
    DateChangeListener,
    ItemClickListener {

    private lateinit var bindingOrderHistoryFragment : OrderHistoryFragmentBinding

    private var orderHistoryViewModel : OrderHistoryViewModel? = null

    private var mSelectedDate : String = ""

    private var mProgressDialog : AlertDialog? = null


    companion object {
        fun newInstance() = OrderHistoryFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingOrderHistoryFragment = DataBindingUtil.inflate(inflater,R.layout.order_history_fragment, container, false)
        bindingOrderHistoryFragment.lifecycleOwner = viewLifecycleOwner
        return bindingOrderHistoryFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        orderHistoryViewModel = OrderHistoryViewModelFactory().getViewModel(false,requireActivity())

        mSelectedDate = FoodsGullyUtils.getDateString(Date(),FoodsGullyConstants.POST_DATE_FORMAT) ?: ""
        initializeOrderHistoryView()
        initializeListeners()
        initializeNoOrdersText()

    }

    private fun initializeListeners() {
        bindingOrderHistoryFragment.ivCalendar.setOnClickListener { showDatePicker() }
        bindingOrderHistoryFragment.ivBack.setOnClickListener { findNavController().popBackStack(R.id.nav_home,false) }
    }

    private fun showDatePicker() {
        FGDatePickerDialog(requireContext(),this,null).show(requireActivity().supportFragmentManager,FoodsGullyConstants.TAG_DATE_PICKER)
    }

    private fun initializeNoOrdersText() {
        bindingOrderHistoryFragment.noOrders.tvNoItemMessage.text = (getString(R.string.no_orders_for_this_date))
    }

    override fun onResume() {
        super.onResume()
        fetchOrders()
    }

    private fun fetchOrders() {

        orderHistoryViewModel?.fetchOrders(mSelectedDate,requireContext())?.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            when (response) {

                is APILoader -> {
                    mProgressDialog = FoodsGullyUtils.showLoaderProgress(
                        getString(R.string.fetching_your_orders),
                        requireContext()
                    )
                }

                is APIError -> {
                    FoodsGullyUtils.dismissProgress(mProgressDialog)
                    FoodsGullyUtils.showSnackbar(
                        response.errorMessage,
                        bindingOrderHistoryFragment.root
                    )
                }

                is Success<*> -> {
                    FoodsGullyUtils.dismissProgress(mProgressDialog)
                    val ordersList = response.data as? List<Order>
                    orderHistoryViewModel?.setOrders(ordersList ?: ArrayList())
                    showOrdersList(!ordersList.isNullOrEmpty())
                    setDateValue()

                }
            }
        })
    }

    private fun showOrdersList(toShow: Boolean) {
        bindingOrderHistoryFragment.noOrders.noItemParentLayout.visibility = if(toShow) View.GONE else View.VISIBLE
        bindingOrderHistoryFragment.parentOrdersLayout.visibility = if(toShow) View.VISIBLE else View.GONE
    }


    private fun setDateValue() {
        bindingOrderHistoryFragment.tvOrderDate.text = orderHistoryViewModel?.getDeliveryDisplayDate(mSelectedDate)
    }

    private fun initializeOrderHistoryView() {

        bindingOrderHistoryFragment.rvOrders.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val adapter = orderHistoryViewModel?.getAdapter()
        adapter?.setVariablesMap(hashMapOf(BR.itemClickListener to this))

        bindingOrderHistoryFragment.rvOrders.setHasFixedSize(true)
        bindingOrderHistoryFragment.rvOrders.adapter = adapter
    }

    override fun onItemClick(item: Any?) {
        findNavController().navigate(R.id.nav_order_details, bundleOf(FoodsGullyConstants.ARG_ORDER to item as? Order))
    }

    override fun onDateChanged(date: String?) {
        mSelectedDate = date ?: ""
        fetchOrders()

    }



}