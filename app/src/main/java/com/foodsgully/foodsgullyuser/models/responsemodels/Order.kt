package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("orderId")
    val orderId: String? = null,

    @SerializedName("userDetails")
    val user: User? = null,

    @SerializedName("orderDetails")
    val orderDetails: OrderDetails? = null

) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readParcelable<User>(User::class.java.classLoader),
        source.readParcelable<OrderDetails>(OrderDetails::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(orderId)
        writeParcelable(user, 0)
        writeParcelable(orderDetails, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Order> = object : Parcelable.Creator<Order> {
            override fun createFromParcel(source: Parcel): Order = Order(source)
            override fun newArray(size: Int): Array<Order?> = arrayOfNulls(size)
        }
    }
}