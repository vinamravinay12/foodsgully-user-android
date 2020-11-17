package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class OrderDetails(
    @SerializedName("assignedDeliveryPersonId")
    val assignedDeliveryPersonId: String? = null,

    @SerializedName("deliveryAddress")
    val deliveryAddress: HomeAddress? = null,

    @SerializedName("deliveryDate")
    val deliveryDate: String? = null,

    @SerializedName("deliveryStatus")
    val deliveryStatus: String? = null,

    @SerializedName("deliveryTime")
    val deliveryTime: String? = null,

    @SerializedName("orderAmount")
    val orderAmount: Double = 0.0,

    @SerializedName("orderNumber")
    val orderNumber: Int = 0,

    @SerializedName("paymentId")
    val paymentId: String? = null,

    @SerializedName("paymentMode")
    val paymentMode: String? = null,

    @SerializedName("paymentStatus")
    val paymentStatus: String? = null,

    @SerializedName("totalQuantities")
    val totalQuantities: String? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("items")
    val orderItems: List<OrderItem>? = null

) : Parcelable {
    constructor(source: Parcel) : this(
    source.readString(),
    source.readParcelable<HomeAddress>(HomeAddress::class.java.classLoader),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readDouble(),
    source.readInt(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.createTypedArrayList(OrderItem.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(assignedDeliveryPersonId)
        writeParcelable(deliveryAddress, 0)
        writeString(deliveryDate)
        writeString(deliveryStatus)
        writeString(deliveryTime)
        writeDouble(orderAmount)
        writeInt(orderNumber)
        writeString(paymentId)
        writeString(paymentMode)
        writeString(paymentStatus)
        writeString(totalQuantities)
        writeString(userId)
        writeTypedList(orderItems)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderDetails> = object : Parcelable.Creator<OrderDetails> {
            override fun createFromParcel(source: Parcel): OrderDetails = OrderDetails(source)
            override fun newArray(size: Int): Array<OrderDetails?> = arrayOfNulls(size)
        }
    }

    fun getOrderNumberToDisplay() : String {
       return when {
           orderNumber < 10 -> "#00$orderNumber"
           orderNumber in 10..99 -> "#0$orderNumber"
           else -> "#$orderNumber"
       }


    }
}