package com.example.kaspintest.dataparcel

import android.os.Parcel
import android.os.Parcelable

data class ItemData(
    var name    : String?,
    var id      : String?,
    var code    : String?,
    var stock   : String?,
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeString(code)
        parcel.writeString(stock)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemData> {
        override fun createFromParcel(parcel: Parcel): ItemData {
            return ItemData(parcel)
        }

        override fun newArray(size: Int): Array<ItemData?> {
            return arrayOfNulls(size)
        }
    }
}
