package com.foodsgully.foodsgullyuser.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foodsgully.foodsgullyuser.fragments.CheckoutFragment
import com.foodsgully.foodsgullyuser.models.CartItem
import com.foodsgully.foodsgullyuser.models.GenericResponse
import com.foodsgully.foodsgullyuser.models.postdatamodels.CreateOrderData
import com.foodsgully.foodsgullyuser.models.postdatamodels.UpdatePaymentStatus
import com.foodsgully.foodsgullyuser.models.responsemodels.HomeAddress
import com.foodsgully.foodsgullyuser.models.responsemodels.OrderItem
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import com.foodsgully.foodsgullyuser.viewmodels.repositories.CreateOrderRepository
import com.foodsgully.foodsgullyuser.viewmodels.repositories.UpdatePaymentStatusRepository
import java.util.*
import kotlin.collections.ArrayList


enum class TimeRangeForDelivery(private var isEligible : Boolean) {

    EARLY_MORNING(true),MORNING(true),AFTERNOON(true),EVENING(true);

}


enum class PaymentMode( val paymentMode : String) {

    ONLINE("online"),CASH("cash")
}

enum class PaymentStatus( val status : String) {
    PAID("paid"),NOT_PAID("not paid")
}
class CheckoutViewModel(private val totalAmount : Double) : ViewModel() {


    private val deliveryAddressData = MutableLiveData<HomeAddress>()

    private val paymentId = MutableLiveData<String>("testPayment123")
    private val paymentMode = MutableLiveData<String>()
    private val selectedItemsInCart = LinkedList<CartItem>().toMutableList()

    private val deliveryDate  = MutableLiveData<String>()
    private val deliveryTime = MutableLiveData<String>()
    private val paymentStatus  = MutableLiveData<String>()
    private val orderId  = MutableLiveData<String>()

    private val cartItems = ArrayList<CartItem>().toMutableList()

    fun getDeliveryAddress() = deliveryAddressData
    fun getPaymentMode() = paymentMode
    fun getSelectedItemsInCart() = selectedItemsInCart

    fun getDeliveryDate() = deliveryDate

    fun getDeliveryTime() = deliveryTime

    fun getPaymentStatus() = paymentStatus

    fun getOrderId() = orderId

    fun setPaymentIdAndStatus(paymentId: String?,paymentStatus: String?) {
        this.paymentId.value = paymentId
        this.paymentStatus.value = paymentStatus
    }

    private fun getTotalItems() = cartItems.map { it.totalQuantity }.sum()
    fun getTotalAmount() = totalAmount


    private val inEligibleDateRanges = ArrayList<TimeRangeForDelivery>().toMutableList()
    fun getCartItems() = cartItems



    fun getEligibleTimePeriodForToday(): ArrayList<TimeRangeForDelivery> {
        val eligibleTimeRanges = ArrayList<TimeRangeForDelivery>()
        inEligibleDateRanges.clear()

       val currentTime = FoodsGullyUtils.getDate(FoodsGullyUtils.getDateString(Date(),FoodsGullyConstants.POST_DATE_TIME_FORMAT),FoodsGullyConstants.POST_DATE_TIME_FORMAT)

        if(checkIfEarlyMorningTimeRangeEligible(currentTime)) eligibleTimeRanges.add(TimeRangeForDelivery.EARLY_MORNING) else inEligibleDateRanges.add(TimeRangeForDelivery.EARLY_MORNING)
        if(checkIfMorningTimeRangeEligible(currentTime)) eligibleTimeRanges.add(TimeRangeForDelivery.MORNING) else inEligibleDateRanges.add(TimeRangeForDelivery.MORNING)
        if(checkIfAfternoonTimeRangeEligible(currentTime)) eligibleTimeRanges.add(TimeRangeForDelivery.AFTERNOON) else inEligibleDateRanges.add(TimeRangeForDelivery.AFTERNOON)
        if(checkIfEveningTimeRangeEligible(currentTime)) eligibleTimeRanges.add(TimeRangeForDelivery.EVENING) else inEligibleDateRanges.add(TimeRangeForDelivery.EVENING)

        return eligibleTimeRanges

    }

    fun getInEligibleTimePeriodForDay() = inEligibleDateRanges

    private fun checkIfEarlyMorningTimeRangeEligible(currentTime : Date?) : Boolean {


        val startTime = getDate(7,0,0)
        val endTime = getDate(10,0,0)

        val differenceBetweenStartAndCurrentTime = getDifferenceInHours(startTime,currentTime ?: Date())

        val differenceBetweenEndAndCurrentTime = getDifferenceInHours(endTime,currentTime ?: Date())
        return differenceBetweenStartAndCurrentTime >= 4 || differenceBetweenEndAndCurrentTime >=4


    }


    private fun checkIfMorningTimeRangeEligible(currentTime : Date?) : Boolean {

        val startTime = getDate(10,30,0)
        val endTime = getDate(14,30,0)

        val differenceBetweenStartAndCurrentTime = getDifferenceInHours(startTime,currentTime ?: Date())

        val differenceBetweenEndAndCurrentTime = getDifferenceInHours(endTime,currentTime ?: Date())
        return differenceBetweenStartAndCurrentTime >= 4 || differenceBetweenEndAndCurrentTime >=4


    }


    private fun checkIfAfternoonTimeRangeEligible(currentTime : Date?) : Boolean {

        val startTime = getDate(15,0,0)
        val endTime = getDate(18,0,0)

        val differenceBetweenStartAndCurrentTime = getDifferenceInHours(startTime,currentTime ?: Date())

        val differenceBetweenEndAndCurrentTime = getDifferenceInHours(endTime,currentTime ?: Date())
        return differenceBetweenStartAndCurrentTime >= 4 || differenceBetweenEndAndCurrentTime >=4


    }

    private fun checkIfEveningTimeRangeEligible(currentTime : Date?) : Boolean {

        val startTime = getDate(18,30,0)
        val endTime = getDate(20,0,0)

        val differenceBetweenStartAndCurrentTime = getDifferenceInHours(startTime,currentTime ?: Date())

        val differenceBetweenEndAndCurrentTime = getDifferenceInHours(endTime,currentTime ?: Date())
        return differenceBetweenStartAndCurrentTime >= 4 || differenceBetweenEndAndCurrentTime >=4


    }


    private fun getDate(hour : Int, minutes : Int, seconds : Int) : Date {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minutes)
       calendar.set(Calendar.SECOND,seconds)
        return calendar.time
    }

    private fun getDifferenceInHours(startTime : Date, endTime : Date) : Long {

        val diff: Long = startTime.time - endTime.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        return minutes / 60

    }


     fun validateTimeRange() : Boolean {
         return deliveryTime.value?.isEmpty() == false
     }

    fun validateHomeAddress() : Boolean {
        return deliveryAddressData.value != null && deliveryAddressData.value?.streetAddress?.isEmpty() == false
                && deliveryAddressData.value?.latitude?.isFinite() == true && deliveryAddressData.value?.longitude?.isFinite() == true
    }

    fun isPaymentModeOnline() : Boolean = paymentMode.value == PaymentMode.ONLINE.paymentMode

     fun placeOrder(context: Context) : MutableLiveData<GenericResponse> {
         val createOrderData = CreateOrderData(paymentId = paymentId.value ?: "testPayment123",paymentStatus = paymentStatus.value ?: PaymentStatus.NOT_PAID.status,
             paymentMode = paymentMode.value ?: PaymentMode.CASH.paymentMode,
         orderAmount = totalAmount,deliveryDate = deliveryDate.value ?: "",deliveryTime = deliveryTime.value ?: "",totalQuantities = getTotalItems(),
         items = cartItems.map { OrderItem(it.selectedProduct,it.totalQuantity,it.itemPrice) },deliveryAddress = deliveryAddressData.value)

         return CreateOrderRepository<String>(createOrderData,context).getResponse()
     }

    fun updatePaymentStatus(paymentId : String?,paymentSuccess : Boolean,context: Context) : MutableLiveData<GenericResponse> {
        val updatePaymentStatus = UpdatePaymentStatus(paymentId,paymentSuccess)
        return UpdatePaymentStatusRepository<String>(updatePaymentStatus,context).getResponse()
    }

    fun setOrderId(orderId: String?) {
        this.orderId.value = orderId
    }


}