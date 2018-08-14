package com.example.track.trackeria;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoadCoord implements Parcelable {
    @SerializedName("snappedPoints")
    private List<Snappoints> points;



    public List<Snappoints> getPoints() {
        return points;
    }

    public void setPoints(List<Snappoints> points) {
        this.points = points;
    }

    protected RoadCoord(Parcel in) {
        this.points = in.createTypedArrayList(Snappoints.CREATOR);
    }

    public static final Creator<RoadCoord> CREATOR = new Creator<RoadCoord>() {
        @Override
        public RoadCoord createFromParcel(Parcel in) {
            return new RoadCoord(in);
        }

        @Override
        public RoadCoord[] newArray(int size) {
            return new RoadCoord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(this.points);
    }
}
