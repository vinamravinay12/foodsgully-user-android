package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.OrderHistoryAdapter
import com.foodsgully.foodsgullyuser.adapters.OrderItemAdapter
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderItem
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.repositories.GetAllOrdersForDateRepository
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryViewModel : ListViewModel<Order>() {


    private var orders = ArrayList<Order>().toMutableList()


    private var adapter : OrderHistoryAdapter

    fun setOrders(orders : List<Order>) {
        this.orders = orders.toMutableList()
        updateList()
    }

    init {
        adapter = OrderHistoryAdapter(orders,getViewType())
    }

    override fun getViewType(): Int = R.layout.card_order_history

    override fun updateList() {
        adapter.updateList(orders)
    }

    override fun getAdapter(): BaseRecyclerAdapter<Order> = adapter


    fun getDeliveryDisplayDate(date : Date?) : String {
        return FoodsGullyUtils.getDateString(date,FoodsGullyConstants.DISPLAY_DATE_FORMAT) ?: ""
    }

    fun getDeliveryDisplayDate(dateString : String?) : String {
        return FoodsGullyUtils.convertDate(dateString,FoodsGullyConstants.POST_DATE_FORMAT,FoodsGullyConstants.DISPLAY_DATE_FORMAT) ?: ""
    }

    fun fetchOrders(date : String,context : Context) : MutableLiveData<GenericResponse> {

        return GetAllOrdersForDateRepository<List<Order>>(date,context).getResponse()
    }

}