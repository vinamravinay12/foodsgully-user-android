package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class HomeAddress(
    @SerializedName("streetAddress")
    val streetAddress: String? = null,

    @SerializedName("houseDetails")
    val houseDetails: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("state")
    val state: String? = null,

    @SerializedName("zip")
    val zip: String? = null,

    @SerializedName("latitude")
    val latitude: Float? = null,

    @SerializedName("longitude")
    val longitude: Float? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readValue(Float::class.java.classLoader) as Float?,
        source.readValue(Float::class.java.classLoader) as Float?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(streetAddress)
        writeString(houseDetails)
        writeString(city)
        writeString(state)
        writeString(zip)
        writeValue(latitude)
        writeValue(longitude)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HomeAddress> = object : Parcelable.Creator<HomeAddress> {
            override fun createFromParcel(source: Parcel): HomeAddress = HomeAddress(source)
            override fun newArray(size: Int): Array<HomeAddress?> = arrayOfNulls(size)
        }
    }
}