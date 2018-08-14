package com.example.track.trackeria;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SnapRoad {

    @GET("snapToRoads?interpolate=true&key=AIzaSyBJDn4_Zh33g9CUxmCPPjFzwUsrWJhsH6c")
    Call<RoadCoord> getCoord(@Query("path") String path);
}
