package ru.geekbrains.notes.ui.auth

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class UserProfile : Parcelable {
    var idToken: String? = null
    var photoURL: String? = null
    var serverAuthCode: String? = null
    var displayName: String? = null
    var email: String? = null
    var familyName: String? = null
    var givenName: String? = null
    var id: String? = null
    var typeAutService = 0

    constructor() {}
    protected constructor(`in`: Parcel) {
        idToken = `in`.readString()
        photoURL = `in`.readString()
        serverAuthCode = `in`.readString()
        displayName = `in`.readString()
        email = `in`.readString()
        familyName = `in`.readString()
        givenName = `in`.readString()
        id = `in`.readString()
        typeAutService = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(idToken)
        dest.writeString(photoURL)
        dest.writeString(serverAuthCode)
        dest.writeString(displayName)
        dest.writeString(email)
        dest.writeString(familyName)
        dest.writeString(givenName)
        dest.writeString(id)
    }

    companion object {
        @JvmField val CREATOR: Creator<UserProfile?> = object : Creator<UserProfile?> {
            override fun createFromParcel(`in`: Parcel): UserProfile? {
                return UserProfile(`in`)
            }

            override fun newArray(size: Int): Array<UserProfile?> {
                return arrayOfNulls(size)
            }
        }
    }
}