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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usc.cargotrackingsystem.POJO.Driver;
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

    private GoogleMap mMap;
    LocationManager mLocationManager;

    Driver driver;
    Transaction selectedTransaction;

    //refresh time for retrieving location in millis
    private final int LOCATION_REFRESH_TIME = 0;

    //refresh distance for retrieving location in meters
    float LOCATION_REFRESH_DISTANCE = 10;

    TextView name;
    Spinner transactionNumberSpinner, truckStatusSpinner;
    Button start, stop;

    Marker m;

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
                    saveLocation(lastLocation);

                startTracking = true;
                start.setEnabled(false);
                stop.setEnabled(true);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking = false;
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        });


        if(driver!=null) {
            name.setText(driver.getFname() + " " + driver.getLname());
            getData();
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            Log.d("Cargo", "Location detected");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markLocationInMap(latLng);

            if(startTracking)
                saveLocation(location);
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

        if(m!=null)
            m.remove();

        m = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
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

    public void saveLocation(final Location location){
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

                String link = "http://" + address +"/saveLocation.php?deliveryId="+ selectedTransaction.getDeliveryID()+ "&latitude="+ location.getLatitude()+"&longitude=" + location.getLongitude();

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
            ArrayList<Transaction> transactionList;

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
                            JSONObject c = jsonArray.getJSONObject(i);

                            Transaction transaction = new Transaction();

                            transaction.setId(c.getString(Transaction.ID_TAG));
                            transaction.setStatus(c.getString(Transaction.STATUS_TAG));
                            transaction.setDeliveryID(c.getString(Transaction.DELIVERY_ID_TAG));

                            if(transactionList == null && !transaction.getStatus().equalsIgnoreCase("delivered"))
                                transactionList = new ArrayList<>();

                            transactionList.add(transaction);
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

                if(transactionList!=null){

                    ArrayList<String> transactionIDList = new ArrayList<>();
                    for(Transaction transaction : transactionList){
                        transactionIDList.add(transaction.getId());
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
                                    selectedTransaction = transactionList.get(position);
                                    start.setEnabled(true);
                                }
                            }).init();

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

                if(dialog!=null)
                    dialog.dismiss();
            }
        }.execute();
    }
}
