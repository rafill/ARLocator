package com.fyp.rafill.arlocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rafill on 17/04/2017.
 */

public class Places implements Parcelable {

    private String _placeID;
    private String _name;
    private String _address;
    private String _lat;
    private String _lng;
    private String _rating;
    private String _priceLevel;
    private String _icon;

    public Places(String id, String name, String address, String lat, String lng, String icon) {
        _placeID = id;
        _name = name;
        _address = address;
        _lat = lat;
        _lng = lng;
        _icon = icon;
    }

    protected Places(Parcel in) {
        _placeID = in.readString();
        _name = in.readString();
        _address = in.readString();
        _lat = in.readString();
        _lng = in.readString();
        _rating = in.readString();
        _priceLevel = in.readString();
        if (_icon != null) _icon = in.readString();
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    public String getID() {
        return _placeID;
    }

    public String getName() {
        return _name;
    }

    public String getAddress() {
        return _address;
    }

    public String getLat() {
        return _lat;
    }

    public String getLng() {
        return _lng;
    }

    public String getRating() {
        return _rating;
    }

    public String getPrice() {
        return _priceLevel;
    }

    public String getIcon() {
        return _icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_placeID);
        dest.writeString(_name);
        dest.writeString(_address);
        dest.writeString(_lat);
        dest.writeString(_lng);
        dest.writeString(_rating);
        dest.writeString(_priceLevel);
    }
}
