package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class OrderItem(
    val item: Product? = null,
    val quantitySelected: Int = 0,
    val itemPrice: Double = 0.0
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readParcelable<Product>(Product::class.java.classLoader),
        source.readInt(),
        source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(item, 0)
        writeInt(quantitySelected)
        writeDouble(itemPrice)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderItem> = object : Parcelable.Creator<OrderItem> {
            override fun createFromParcel(source: Parcel): OrderItem = OrderItem(source)
            override fun newArray(size: Int): Array<OrderItem?> = arrayOfNulls(size)
        }
    }
}