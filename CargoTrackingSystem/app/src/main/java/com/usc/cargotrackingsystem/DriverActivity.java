package com.usc.cargotrackingsystem;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager mLocationManager;

    //refresh time for retrieving location in millis
    private final int LOCATION_REFRESH_TIME = 0;

    //refresh distance for retrieving location in meters
    float LOCATION_REFRESH_DISTANCE = 0;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Cargo", "Location detected");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markLocationInMap(latLng);
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
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        markCurrentLocationInMap();
    }

    void markCurrentLocationInMap(){
        int permissionCheck = ContextCompat.checkSelfPermission(DriverActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck ==  PackageManager.PERMISSION_GRANTED){

            Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(lastLocation == null)
                lastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(lastLocation == null){
                markLocationInMap(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        }else{
            ActivityCompat.requestPermissions(DriverActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    void markLocationInMap(LatLng location){
        Bitmap icon =  Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.cargo), 100, 100, false);

        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // location permission was granted, get current location!
                    markCurrentLocationInMap();
                } else {

                    // permission denied, notify user
                    Toast.makeText(DriverActivity.this, "Permission denied, unable to start Map", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


}
