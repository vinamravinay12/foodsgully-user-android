package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("homeAddress")
    val homeAddress: HomeAddress? = null,

    @SerializedName("totalOrders")
    val totalOrders: Int = 0,

    @SerializedName("totalOrderAmount")
    val totalOrderAmount: Float = 0f

) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<HomeAddress>(HomeAddress::class.java.classLoader),
        source.readInt(),
        source.readFloat()
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
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}