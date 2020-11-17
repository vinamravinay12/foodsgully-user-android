package com.foodsgully.foodsgullyuser.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class HomeAddress(
    @SerializedName("streetAddress")
    var streetAddress: String? = "",

    @SerializedName("houseNumber")
    var houseNumber: String? = "",

    @SerializedName("floorNumber")
    var floorNumber: String? = "",

    @SerializedName("landmark")
    var landmark: String? = "",

    @SerializedName("city")
    var city: String? = "",

    @SerializedName("state")
    var state: String? = "",

    @SerializedName("zip")
    var zip: String? = "",

    @SerializedName("latitude")
    var latitude: Double? = null,

    @SerializedName("longitude")
    var longitude: Double? = null
) : Parcelable {
    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readValue(Float::class.java.classLoader) as Double?,
    source.readValue(Float::class.java.classLoader) as Double?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(streetAddress)
        writeString(houseNumber)
        writeString(floorNumber)
        writeString(landmark)
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

    fun getCityName() : String {
        return if(city.isNullOrEmpty()) "No Location" else city ?: ""
    }


}