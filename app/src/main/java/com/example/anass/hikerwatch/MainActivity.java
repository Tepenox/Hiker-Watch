package com.example.anass.hikerwatch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView latTextView;
    TextView lonTextView;
    TextView altTextView;
    TextView accTextView;
    TextView addressTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latTextView);

        lonTextView = findViewById(R.id.lonTextView);

        altTextView = findViewById(R.id.altTextView);

        accTextView = findViewById(R.id.accTextView);

        addressTextView = findViewById(R.id.addressTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        checkLocationPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay! Do the
            // location-related task you need to do.
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
            }
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("GPS permission")
                        .setMessage("a Location permission is required for this app to work")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);

//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if (location != null) {
//
//                updateLocationInfo(location);
//
//            }
            return true;
        }
    }

    private void updateLocationInfo(Location location) {

        Log.i("i", location.toString());

        latTextView.setText("Latitude: " + location.getLatitude());

        lonTextView.setText("Longitude: " + location.getLongitude());

        altTextView.setText("Altitude: " + location.getAltitude());

        accTextView.setText("Accuracy: " + location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address;

        List<Address> listAddresses = null;
        try {
            //retrieving the results and returning a list of near addresses  int his case we only returning the first result
            listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {

            e.printStackTrace();
        }

        if (listAddresses != null && listAddresses.size() > 0) {


            address = "Address: \n";

            if (listAddresses.get(0).getSubThoroughfare() != null) {

                address += listAddresses.get(0).getSubThoroughfare() + " ";

            }

            if (listAddresses.get(0).getThoroughfare() != null) {

                address += listAddresses.get(0).getThoroughfare() + "\n";

            }

            if (listAddresses.get(0).getLocality() != null) {

                address += listAddresses.get(0).getLocality() + "\n";

            }

            if (listAddresses.get(0).getPostalCode() != null) {

                address += listAddresses.get(0).getPostalCode() + "\n";

            }

            if (listAddresses.get(0).getCountryName() != null) {

                address += listAddresses.get(0).getCountryName() + "\n";

            }

        } else {
            address = "Could not find address"; //the geocoder couldn t find an address in your location and has 0 results
        }

        addressTextView.setText(address);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(locationListener);
        }
    }
}
