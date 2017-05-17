package com.andresdavid.testgeofencinggideoracle;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by andresdavid on 13/09/16.
 */
public class GeofencingService extends IntentService {
    protected static final String LOG_TAG = GeofencingService.class.getName();

    public GeofencingService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError())
        {
            Log.e(LOG_TAG,"geofencingEvent.hasError()");
        }
        else{
            int transition = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofenceList.get(0);
            String requestId = geofence.getRequestId();
            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
                Log.d(LOG_TAG,String.format("GEOFENCE_TRANSITION_ENTER %s",requestId));
            }
            else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                Log.d(LOG_TAG,String.format("GEOFENCE_TRANSITION_EXIT %s",requestId));
            }
        }

    }
}
