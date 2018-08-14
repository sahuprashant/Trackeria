package com.example.track.trackeria;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Snappoints implements Parcelable {
    @SerializedName("location")
    private PointLocation pointLocation;

    @SerializedName("placeId")
    private String placeId;

    public Snappoints(PointLocation pointLocation,String placeId){
        this.pointLocation = pointLocation;
        this.placeId = placeId;
    }

    public PointLocation getPointLocation() {
        return pointLocation;
    }

    public void setPointLocation(PointLocation pointLocation) {
        this.pointLocation = pointLocation;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    protected Snappoints(Parcel in) {
        placeId = in.readString();
    }

    public static final Creator<Snappoints> CREATOR = new Creator<Snappoints>() {
        @Override
        public Snappoints createFromParcel(Parcel in) {
            return new Snappoints(in);
        }

        @Override
        public Snappoints[] newArray(int size) {
            return new Snappoints[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeId);
    }
}
