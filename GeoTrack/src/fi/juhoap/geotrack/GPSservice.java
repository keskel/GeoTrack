package fi.juhoap.geotrack;

/*
 * class for the gps service / broadcaster
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;

public class GPSservice extends Service implements LocationListener {

	private final Context mContext;

	Location location; // location
	double latitude = 0; // latitude
	double longitude = 0; // longitude
	
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE = 10; // 10 meters

	// The minimum time between updates in milliseconds
	//private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	private static final long MIN_TIME = 1000 * 10; // 10 seconds

	private static final String LATITUDE = "loc_lat";
	private static final String LONGITUDE = "loc_lon";

	// Declaring a Location Manager
	protected LocationManager locationManager;
   
	public GPSservice(Context context) {
		this.mContext = context;
		getLocation();
	}
	
    private void getLocation() {
    	
    	locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
		
    	// set some criteria for the data we want to get
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, false);
        
        locationManager.requestLocationUpdates(provider, MIN_DISTANCE, MIN_TIME, this);
        
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
    
    // when we get new coordinates, we broadcast them to possible listeners
    @Override
    public void onLocationChanged(final Location location) {
    	//Log.d("sender", "Broadcasting message");
    	Intent intent = new Intent("gps-location");
    	intent.putExtra(LATITUDE, location.getLatitude());
    	intent.putExtra(LONGITUDE, location.getLongitude());
    	LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    
    @Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    
    /**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if(location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if(location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null) {
			locationManager.removeUpdates(GPSservice.this);
		}
	}
	
}
