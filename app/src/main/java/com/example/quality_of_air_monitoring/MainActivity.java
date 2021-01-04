package com.example.QualityOfAirMonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    /* @BindView(R.id.activity_main_bottom_navigation)
    BottomNavigationView bottomNavigationView; */
    BottomNavigationView bottomNavigation;

    public static final int PERMISSIONS_REQUEST = 0;

    public static final String LOCATION = "location";

    private Location mLocation;
    private String locationProvider;

    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""));

        // Set location provider.
        locationProvider = LocationManager.GPS_PROVIDER;
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the location provider
                Toast.makeText(getApplicationContext(), "Getting your location...",
                        Toast.LENGTH_SHORT).show();
                mLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        requestLocationUpdates();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }*/

    @Override
    public void onRequestPermissionsResult(int request, String permissions[], int[] results) {
        switch (request) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay! Do something useful
                    requestLocationUpdates();
                } else {
                    // Permission was denied, boo! Disable the
                    // functionality that depends on this permission
                    Toast.makeText(this, "Permission denied to access device's location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        // Check permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            locationManager.requestLocationUpdates(
                    locationProvider, 1000, 1, locationListener);
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_monitor:
                            openFragment(MonitorFragment.newInstance(mLocation.getLatitude(), mLocation.getLongitude()));
                            return true;
                        case R.id.navigation_history:
                            openFragment(HistoryFragment.newInstance());
                            return true;
                    }
                    return false;
                }
            };
}
