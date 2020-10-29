package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable

data class Category(
    val categoryNumber: Int = -1,
    val categoryName: String? = null,
    val categoryImage: String? = "",
    val categoryUrl: String? = "",
    val categoryColor: String? = ""

) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(categoryNumber)
        writeString(categoryName)
        writeString(categoryImage)
        writeString(categoryUrl)
        writeString(categoryColor)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Category> = object : Parcelable.Creator<Category> {
            override fun createFromParcel(source: Parcel): Category = Category(source)
            override fun newArray(size: Int): Array<Category?> = arrayOfNulls(size)
        }
    }
}