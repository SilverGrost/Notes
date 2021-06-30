package ru.geekbrains.notes.ui.auth;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class UserProfile implements Parcelable {
    private String idToken;
    private String photoURL;
    private String serverAuthCode;
    private String displayName;
    private String email;
    private String familyName;
    private String givenName;
    private String iD;
    private int typeAutService;

    public UserProfile() {
    }


    protected UserProfile(Parcel in) {
        idToken = in.readString();
        photoURL = in.readString();
        serverAuthCode = in.readString();
        displayName = in.readString();
        email = in.readString();
        familyName = in.readString();
        givenName = in.readString();
        iD = in.readString();
        typeAutService = in.readInt();
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    public int getTypeAutService() {
        return typeAutService;
    }

    public void setTypeAutService(int typeAutService) {
        this.typeAutService = typeAutService;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getServerAuthCode() {
        return serverAuthCode;
    }

    public void setServerAuthCode(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIdToken());
        //dest.writeString(getPhotoURL());
        dest.writeString(getServerAuthCode());
        dest.writeString(getDisplayName());
        dest.writeString(getEmail());
        dest.writeString(getFamilyName());
        dest.writeString(getGivenName());
        dest.writeString(getiD());
    }
}
