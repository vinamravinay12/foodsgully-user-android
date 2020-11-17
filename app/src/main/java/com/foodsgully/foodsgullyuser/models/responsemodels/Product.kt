package com.foodsgully.foodsgullyuser.models.responsemodels

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Product(
    val productId: String? = null,
    val productNumber: Int = -1,
    val sequenceInCategory: Int = -1,
    val type: String? = null,
    val subType: String? = null,
    val category: String? = null,
    val productMode: String? = null,
    val description: String? = null,
    val quantity: String? = null,
    val quantityType: String? = null,
    val discountPercent: Int = 0,
    val mrp: String? = null,
    val salePrice: String? = null,
    val image: String? = null,
    val availability: Boolean = false,
    val imageUrl: String? = null,
    var categoryImageBitmap: Bitmap? = null

) : Parcelable {
    constructor(source: Parcel) : this(
    source.readString(),
    source.readInt(),
    source.readInt(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readInt(),
    source.readString(),
    source.readString(),
    source.readString(),
    1 == source.readInt(),
    source.readString(),
    source.readParcelable<Bitmap>(Bitmap::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(productId)
        writeInt(productNumber)
        writeInt(sequenceInCategory)
        writeString(type)
        writeString(subType)
        writeString(category)
        writeString(productMode)
        writeString(description)
        writeString(quantity)
        writeString(quantityType)
        writeInt(discountPercent)
        writeString(mrp)
        writeString(salePrice)
        writeString(image)
        writeInt((if (availability) 1 else 0))
        writeString(imageUrl)
        writeParcelable(categoryImageBitmap, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
            override fun createFromParcel(source: Parcel): Product = Product(source)
            override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
        }
    }
}