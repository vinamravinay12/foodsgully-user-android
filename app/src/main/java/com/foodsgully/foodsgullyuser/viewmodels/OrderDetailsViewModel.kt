package com.foodsgully.foodsgullyuser.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.adapters.BaseRecyclerAdapter
import com.foodsgully.foodsgullyuser.adapters.OrderItemAdapter
import com.foodsgully.foodsgullyuser.models.responsemodels.Order
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderItem
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils


enum class DeliveryStatus(val status: String) {
        NOT_ASSIGNED("Confirmed"),ASSIGNED("Accepted"), DELIVERING("Out for delivery"), DELIVERED("Delivered")


}


class OrderDetailsViewModel(private val order : Order) : ListViewModel<OrderItem>() {

    private var orderData = MutableLiveData<Order>()


    private var orderItemAdapter : OrderItemAdapter

    init {
        orderItemAdapter = OrderItemAdapter(orderData.value?.orderDetails?.orderItems?.toMutableList() ?: ArrayList<OrderItem>().toMutableList(),getViewType())
    }


    fun getOrders() = orderData.value


    fun setOrders(order : Order?) {
        orderData.value = order
    }



    override fun getViewType(): Int = R.layout.card_ordered_item

    override fun getAdapter(): BaseRecyclerAdapter<OrderItem> = orderItemAdapter

    override fun updateList() {
        orderItemAdapter.updateList(orderData.value?.orderDetails?.orderItems?.toMutableList())
    }


    fun getDeliveryDisplayDate()  : String {
        return FoodsGullyUtils.convertDate(orderData.value?.orderDetails?.deliveryDate,
            FoodsGullyConstants.POST_DATE_FORMAT,
            FoodsGullyConstants.DISPLAY_DATE_FORMAT) ?: ""
    }


    fun getDeliveryStatus() : String {

        var deliveryStatus =  when(orderData.value?.orderDetails?.deliveryStatus) {
            DeliveryStatus.ASSIGNED.name -> DeliveryStatus.ASSIGNED.status
            DeliveryStatus.NOT_ASSIGNED.name -> DeliveryStatus.NOT_ASSIGNED.status
            DeliveryStatus.DELIVERING.name -> DeliveryStatus.DELIVERING.status
            DeliveryStatus.DELIVERED.name -> DeliveryStatus.DELIVERED.status
            else -> ""

        }

        return "Delivery Status: $deliveryStatus"
    }


    fun getPaymentStatus() : String {
        return orderData.value?.orderDetails?.paymentStatus?.capitalize() ?: ""
    }

    fun getOrderTotal() : String {
        val sum = orderData.value?.orderDetails?.orderItems?.map { it.itemPrice }?.sum()
        return "$sum"
    }

    fun getGrandTotal() : String {
        return getOrderTotal()
    }




}