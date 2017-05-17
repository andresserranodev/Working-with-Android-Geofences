package com.andresdavid.testgeofencinggideoracle;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.location.LocationServices.API;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String LOG_TAG = MainActivity.class.getName();
    private static final String GEOFENCE_ID = "myGeofenceID";
    private GoogleApiClient googleApiClient;

    private double LATITUDE = 4.6613632;
    private double LONGITUDE = -74.133357;
    private float RADIO = 100;


    private Button starLocationMonitor, stargeofencemonitor, stopgeofencemonitor;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        starLocationMonitor = (Button) findViewById(R.id.starLocationMonitor);
        starLocationMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starLocationMonitor();
            }
        });
        stargeofencemonitor = (Button) findViewById(R.id.starGeofenceMonitor);
        stargeofencemonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starGeofenceMonitor();
            }
        });
        stopgeofencemonitor = (Button) findViewById(R.id.stopGeofenceMonitor);
        stopgeofencemonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGeofenceMonitor();
            }
        });
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        buildGoogleApiCliente();
    }

    protected synchronized void buildGoogleApiCliente() {
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS)
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        else {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(AppIndex.API).build();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        super.onStart();

    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnecting() || googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();

        client.disconnect();
    }


    private void starLocationMonitor() {

        Log.d(LOG_TAG, "starLocationMonitor");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        } catch (Exception e) {
            Log.d(LOG_TAG, String.format("Error starLocationMonitor %s", e.getMessage()));

        }

    }

    private void starGeofenceMonitor() {

        Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(LATITUDE, LONGITUDE, RADIO)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build();
        Intent intent = new Intent(this, GeofencingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!googleApiClient.isConnected()) {
            Log.d(LOG_TAG, "GoogleApiClient is not Conectec");
        } else {
            LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(LOG_TAG, "Successfully add geofencingRequest");
                            } else {
                                Log.d(LOG_TAG, "Failed to add geofencingRequest" + status.getStatus());

                            }
                        }
                    });
        }

    }

    private void stopGeofenceMonitor() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, String.format("onLocationChanged New Latitude: %s ,Longitude %s", location.getLatitude(), location.getLongitude()));

    }
}
