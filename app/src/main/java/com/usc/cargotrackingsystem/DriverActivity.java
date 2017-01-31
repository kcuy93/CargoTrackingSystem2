package com.usc.cargotrackingsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usc.cargotrackingsystem.POJO.Driver;
import com.usc.cargotrackingsystem.POJO.Package;
import com.usc.cargotrackingsystem.POJO.Transaction;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1234;
    public static final String OPERATION_START = "OPERATION_START";
    public static final String OPERATION_STOP = "OPERATION_STOP";
    public static final String OPERATION_LOG = "OPERATION_LOG";

    private GoogleMap mMap;
    LocationManager mLocationManager;

    Driver driver;
    Package selectedPackage;

    //refresh time for retrieving location in millis
    private final int LOCATION_REFRESH_TIME = 0;

    //refresh distance for retrieving location in meters
    float LOCATION_REFRESH_DISTANCE = 10;

    TextView name;
    Spinner transactionNumberSpinner, truckStatusSpinner;
    Button start, stop;

    Marker destinationMarker, currentMarker;

    boolean startTracking;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        driver = GlobalData.currentDriver;

        name = (TextView) findViewById(R.id.driver);
        transactionNumberSpinner = (Spinner) findViewById(R.id.transaction_number);
        truckStatusSpinner = (Spinner) findViewById(R.id.truck_status);
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);

        start.setEnabled(false);
        stop.setEnabled(false);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastLocation!=null)
                    saveLocation(lastLocation, OPERATION_START);

                startTracking = true;
                start.setEnabled(false);
                stop.setEnabled(true);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastLocation!=null)
                    saveLocation(lastLocation, OPERATION_STOP);
                startTracking = false;
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        });

        if(driver!=null) {
            name.setText(driver.getFname() + " " + driver.getLname());
            getData();
        }

        ArrayList<String> truckStatus = new ArrayList<>();
        truckStatus.add("Available");
        truckStatus.add("Unavailable");

        new HintSpinner<>(
                truckStatusSpinner,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data
                new HintAdapter<>(DriverActivity.this, "Truck Status", truckStatus),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {

                    }
                }).init();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            Log.d("Cargo", "Location detected");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markLocationInMap(latLng);

            if(startTracking)
                saveLocation(location, OPERATION_LOG);
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

            this.lastLocation = lastLocation;

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
                R.drawable.car), 74, 40, false);

        if(currentMarker!=null)
            currentMarker.remove();

        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
        );

        if(destinationMarker !=null){
            moveCamera();
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
        }

    }

    void markDestinationInMap(LatLng location){

        if(destinationMarker!=null)
            destinationMarker.remove();

        destinationMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Destination Location")
        );

        if(currentMarker!=null){
            moveCamera();
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
        }
    }

    void moveCamera(){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //the include method will calculate the min and max bound.
        if(currentMarker!=null)
            builder.include(currentMarker.getPosition());

        if(destinationMarker!=null)
            builder.include(destinationMarker.getPosition());

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void saveLocation(final Location location, final String operationType){
        new AsyncTask<Void, Void, Void>(){

            String result;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
            @Override
            protected Void doInBackground(Void... voids) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(DriverActivity.this);
                String hostname = sharedPref.getString("hostname", "");
                String portNumber = sharedPref.getString("portNumber", "");

                String address;

                if(portNumber.isEmpty())
                    address = hostname;
                else
                    address = hostname + ":" + portNumber;

                String link = "http://" + address +"/saveLocation.php?transactionID="+ selectedPackage.getTransactionID()+ "&latitude="+ location.getLatitude()+"&longitude=" + location.getLongitude()
                        + "&packageID=" + selectedPackage.getPackageID()+ "&operationType=" + operationType;

                result = PHPHelper.phpGet(link);

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }

        }.execute();

    }


    public void getData(){
        new AsyncTask<Void, Void, Void>(){

            ProgressDialog dialog;
            JSONArray jsonArray = null;
            String result;
            ArrayList<Package> packageList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(DriverActivity.this, "Log in",
                        "Authenticating...", true);
            }
            @Override
            protected Void doInBackground(Void... voids) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(DriverActivity.this);
                String hostname = sharedPref.getString("hostname", "");
                String portNumber = sharedPref.getString("portNumber", "");

                String address;

                if(portNumber.isEmpty())
                    address = hostname;
                else
                    address = hostname + ":" + portNumber;

                String link = "http://" + address +"/getTransactions.php?driverId="+driver.getId();

                result = PHPHelper.phpGet(link);

                try {

                    if(result!=null){
                        JSONObject jsonObj = new JSONObject(result);

                        jsonArray = jsonObj.getJSONArray("result");

                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject packageJson = jsonArray.getJSONObject(i);

                            Package packageObj = new Package();

                            packageObj.setPackageID(packageJson.getString(Package.TAG_PACKAGE_ID));
                            packageObj.setTransactionID(packageJson.getString(Package.TAG_TRANSACTION_ID));

                            JSONObject deliveryInfo = packageJson.getJSONObject("deliveryInfo");
                            packageObj.setDestinationLatitude(deliveryInfo.getString("destLat"));
                            packageObj.setDestinationLongitude(deliveryInfo.getString("destLong"));

                            if(packageList == null)
                                packageList = new ArrayList<Package>();

                            packageList.add(packageObj);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(packageList!=null){

                    ArrayList<String> transactionIDList = new ArrayList<>();
                    for(Package pack : packageList){
                        transactionIDList.add(pack.getPackageID());
                    }

                    new HintSpinner<>(
                            transactionNumberSpinner,
                            // Default layout - You don't need to pass in any layout id, just your hint text and
                            // your list data
                            new HintAdapter<>(DriverActivity.this, "Select Transaction", transactionIDList),
                            new HintSpinner.Callback<String>() {
                                @Override
                                public void onItemSelected(int position, String itemAtPosition) {
                                    // Here you handle the on item selected event (this skips the hint selected event)
                                    selectedPackage = packageList.get(position);

                                    markDestinationInMap(new LatLng(Double.valueOf(selectedPackage.getDestinationLatitude()), Double.valueOf(selectedPackage.getDestinationLongitude())));

                                    start.setEnabled(true);
                                }
                            }).init();
                }

                if(dialog!=null)
                    dialog.dismiss();
            }
        }.execute();
    }
}
