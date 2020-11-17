package com.foodsgully.foodsgullyuser.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.databinding.OrderDetailsFragmentBinding
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.viewmodels.OrderDetailsViewModel
import com.foodsgully.foodsgullyuser.viewmodels.factories.OrderDetailsViewModelFactory

class OrderDetailsFragment : Fragment() {

    private lateinit var bindingOrderDetailsFragment: OrderDetailsFragmentBinding
    private  var viewModel: OrderDetailsViewModel? = null

    private var mSelectedOrder : Order? = null



    companion object {
        fun newInstance() = OrderDetailsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            mSelectedOrder = getParcelable(FoodsGullyConstants.ARG_ORDER) as? Order
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingOrderDetailsFragment = DataBindingUtil.inflate(inflater,R.layout.order_details_fragment, container, false)
        bindingOrderDetailsFragment.lifecycleOwner = viewLifecycleOwner
        return bindingOrderDetailsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = OrderDetailsViewModelFactory(mSelectedOrder).getViewModel(true,requireActivity())

        viewModel?.setOrders(mSelectedOrder)
       bindingOrderDetailsFragment.orderDetailsVM = viewModel

        initializeOrderItemsView()
        initializeListeners()
        viewModel?.updateList()
    }

    private fun initializeListeners() {
        bindingOrderDetailsFragment.ivBack.setOnClickListener { findNavController().popBackStack(R.id.nav_order_history,false) }
    }

    private fun initializeOrderItemsView() {
        bindingOrderDetailsFragment.rvOrderItems.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val adapter = viewModel?.getAdapter()

        bindingOrderDetailsFragment.rvOrderItems.setHasFixedSize(true)
        bindingOrderDetailsFragment.rvOrderItems.adapter = adapter
    }

}