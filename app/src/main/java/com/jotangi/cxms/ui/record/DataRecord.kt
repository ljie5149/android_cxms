package com.jotangi.cxms.ui.record

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DataRecordResponseII(
    val status: String,
    val code: String,
    val responseMessage: String?
)

data class DataRecordResponse(
    val status: String,
    val code: String,
    val responseMessage: List<DataRecord>?
)

data class DataRecord(
    @SerializedName("id")
    val id: String,

    @SerializedName("source")
    val source: String,

    @SerializedName("time_create")
    val time_create: String,

    @SerializedName("doctor")
    val doctor: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("sum_revise")
    val sum_revise: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("hospital")
    val hospital: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(source)
        parcel.writeString(time_create)
        parcel.writeString(doctor)
        parcel.writeString(phone)
        parcel.writeString(sum_revise)
        parcel.writeString(username)
        parcel.writeString(hospital)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataRecord> {
        override fun createFromParcel(parcel: Parcel): DataRecord {
            return DataRecord(parcel)
        }

        override fun newArray(size: Int): Array<DataRecord?> {
            return arrayOfNulls(size)
        }
    }
}