package com.foodsgully.foodsgullyuser.models.responsemodels

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    var fullName: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,

    @SerializedName("homeAddress")
    var homeAddress: HomeAddress? = null,

    @SerializedName("totalOrders")
    val totalOrders: Int = 0,

    @SerializedName("totalOrderAmount")
    val totalOrderAmount: Float = 0f,

    @SerializedName("imageUrl")
    var imageUrl: String? = null,

    var image: Bitmap? = null

) : Parcelable {
    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readParcelable<HomeAddress>(HomeAddress::class.java.classLoader),
    source.readInt(),
    source.readFloat(),
    source.readString(),
    source.readParcelable<Bitmap>(Bitmap::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(userId)
        writeString(email)
        writeString(fullName)
        writeString(phoneNumber)
        writeParcelable(homeAddress, 0)
        writeInt(totalOrders)
        writeFloat(totalOrderAmount)
        writeString(imageUrl)
        writeParcelable(image, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}