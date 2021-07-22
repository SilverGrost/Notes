package ru.geekbrains.notes.note

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class Note : Parcelable {
    var value: String? = null
    var iD: Int
    var idCloud: String? = null
    var dateEdit: Long = 0
    var dateCreate: Long = 0

    constructor() {
        iD = -1
    }

    constructor(value: String?, id: Int, dateEdit: Long, dateCreate: Long) {
        this.value = value
        iD = id
        this.dateEdit = dateEdit
        this.dateCreate = dateCreate
        idCloud = ""
    }

    protected constructor(`in`: Parcel) {
        value = `in`.readString()
        iD = `in`.readInt()
        dateEdit = `in`.readLong()
        dateCreate = `in`.readLong()
        idCloud = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(value)
        dest.writeInt(iD)
        dest.writeLong(dateEdit)
        dest.writeLong(dateCreate)
        dest.writeString(idCloud)
    }

    companion object {
        @JvmField val CREATOR: Creator<Note?> = object : Creator<Note?> {
            override fun createFromParcel(`in`: Parcel): Note? {
                return Note(`in`)
            }

            override fun newArray(size: Int): Array<Note?> {
                return arrayOfNulls(size)
            }
        }
    }
}