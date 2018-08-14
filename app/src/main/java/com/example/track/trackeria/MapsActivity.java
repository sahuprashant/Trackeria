package com.example.track.trackeria;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.util.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    List<LatLng> latLng = new ArrayList<LatLng>();
    String str;
    String TAG = "MapsActivity";
    private FusedLocationProviderClient fusedLocationProviderClient;
    TrackDatabase db;
    double lat;
    double lon;
    int i = 0;
    private String BASE_URL = "https://roads.googleapis.com/v1/";
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = getFusedLocationProviderClient(this);
        db = new TrackDatabase(this);
        Cursor res = db.getData();
        if (res.getCount() == 0){
            Toast.makeText(this,"Location Empty!",Toast.LENGTH_SHORT).show();
        }
        else{
            new plotpoints(res).execute();
        }
        /*locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(2000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        } else {
            requestPermissions();
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        */
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }else{
            requestPermissions();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            latLng = new LatLng(lat, lon);
                            Log.d(TAG, "location is: " + lat + lon);
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                                str = addressList.get(0).getLocality() + ", ";
                                str += addressList.get(0).getCountryName();
                                Log.d(TAG, "Location: " + str);
                                Toast.makeText(MapsActivity.this, "Location: " + str, Toast.LENGTH_SHORT).show();
                                mapFragment.getMapAsync(MapsActivity.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    db.insertdata(lat,lon);
                    latLng = new LatLng(lat, lon);
                    Log.d(TAG, "1 Location: " + lat + lon);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        str = addressList.get(0).getLocality() + ", ";
                        str += addressList.get(0).getCountryName();
                        Log.d(TAG, "from Network Provider Location:  " + str);
                        Toast.makeText(MapsActivity.this, "from Network Provider Location: " + str, Toast.LENGTH_SHORT).show();
                        mapFragment.getMapAsync(MapsActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    db.insertdata(lat,lon);
                    latLng = new LatLng(lat, lon);
                    Log.d(TAG, "1G Location: " + lat + lon);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        str = addressList.get(0).getLocality() + ", ";
                        str += addressList.get(0).getCountryName();
                        Log.d(TAG, "GPS Location: " + str);
                        Toast.makeText(MapsActivity.this, "GPS Location: " + str, Toast.LENGTH_SHORT).show();
                        mapFragment.getMapAsync(MapsActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "GError: " + e.getMessage());
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }*/
    }

    /*public boolean plotpoints(Cursor res){
        int i=0;
        int getcount;
        List<String> paths = new ArrayList<String>();
       // List<Snappoints> snappoints = new ArrayList<>();
        getcount = res.getCount();

        while(res.moveToNext()){
            lat = res.getDouble(0);
            lon = res.getDouble(1);
            //LatLng ltlg = new LatLng(lat,lon);
            //latLng.add(ltlg);
            String latti = String.valueOf(lat);
            String longi = String.valueOf(lon);
            //Log.d(TAG,"lat and lon are: "+latti+longi);
            paths.add(latti+","+longi);
        }
        Joiner join = Joiner.on('|');
        String path = join.join(paths);
        Log.d(TAG,"final path: "+path);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        SnapRoad snapRoad = retrofit.create(SnapRoad.class);
        Call<RoadCoord> call = snapRoad.getCoord(path);
        Log.d(TAG,"call is: "+snapRoad.toString());
        call.enqueue(new Callback<RoadCoord>() {
            @Override
            public void onResponse(Call<RoadCoord> call, Response<RoadCoord> response) {
                List<Snappoints> snappoints = new ArrayList<Snappoints>();
                snappoints = response.body().getPoints();
                Log.d(TAG,"no of points: "+ snappoints.get(0).getPointLocation().getLat());
                int nop = snappoints.size();
                Log.d(TAG,"no of points: "+nop);
                for (int i=0;i<nop;i++){
                    double lat = snappoints.get(i).getPointLocation().getLat();
                    double lon = snappoints.get(i).getPointLocation().getLon();
                    Log.d(TAG,"Fetched lat and lon are: "+lat+lon);
                    LatLng ltlg = new LatLng(lat,lon);
                    latLng.add(ltlg);
                }
                Log.d(TAG,"recieved path: "+latLng.get(0).toString());
            }

            @Override
            public void onFailure(Call<RoadCoord> call, Throwable t) {
                Log.d(TAG,"Failed to fit the path");
            }
        });
        mapFragment.getMapAsync(MapsActivity.this);
        return true;
    }
*/
    private class plotpoints extends AsyncTask<String,Void,String>{
        Cursor res;
        int i=0;
        int getcount;
        List<String> paths = new ArrayList<String>();
        List<Snappoints> snappoints = new ArrayList<Snappoints>();
        public plotpoints(Cursor res){
            this.res = res;
        }

        @Override
        protected String doInBackground(String... strings) {
            getcount = res.getCount();

            while(res.moveToNext()){
                lat = res.getDouble(0);
                lon = res.getDouble(1);
                LatLng ltlg = new LatLng(lat,lon);
                latLng.add(ltlg);
                String latti = String.valueOf(lat);
                String longi = String.valueOf(lon);
                //Log.d(TAG,"lat and lon are: "+latti+longi);
                paths.add(latti+","+longi);
            }
            Joiner join = Joiner.on('|');
            String path = join.join(paths);
            Log.d(TAG,"final path: "+path);
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            SnapRoad snapRoad = retrofit.create(SnapRoad.class);
            Call<RoadCoord> call = snapRoad.getCoord(path);
            Log.d(TAG,"call is: "+snapRoad.toString());
            call.enqueue(new Callback<RoadCoord>() {
                @Override
                public void onResponse(Call<RoadCoord> call, Response<RoadCoord> response) {
                    snappoints = response.body().getPoints();
                    Log.d(TAG,"no of points: "+snappoints.get(0).getPointLocation().getLat());
                    int nop = snappoints.size();
                    Log.d(TAG,"no of points: "+nop);
                    for (int i=0;i<nop;i++){
                        double lat = snappoints.get(i).getPointLocation().getLat();
                        double lon = snappoints.get(i).getPointLocation().getLon();
                        Log.d(TAG,"Fetched lat and lon are: "+lat+lon);
                        LatLng ltlg = new LatLng(lat,lon);
                        latLng.add(ltlg);
                    }
                    Log.d(TAG,"recieved path: "+latLng.toString());
                }

                @Override
                public void onFailure(Call<RoadCoord> call, Throwable t) {
                    Log.d(TAG,"Failed to fit the path");
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mapFragment.getMapAsync(MapsActivity.this);
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Location is following: " + msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{
            mMap.setMyLocationEnabled(true);
        }
        Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(latLng).width(2).color(Color.RED));
        //Log.d(TAG,"onMapReady Location: "+latLng.get(0).latitude);
        //mMap.addMarker(new MarkerOptions().position(latLng).title(str));
        LatLng lt = new LatLng(lat,lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt, 17f));


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapsActivity.this,Main2Activity.class));
        finish();
    }
}
