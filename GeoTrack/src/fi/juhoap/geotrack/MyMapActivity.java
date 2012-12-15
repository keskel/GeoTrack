package fi.juhoap.geotrack;

/** 
 * MAP activity class
 * (sometimes doesn't get data from map server and takes a while to load)
 * - when turning the display, loads all the addresses again.. haven't found a solution yet!
 * - does not update the map when receiving new gps positions (but saves them on the db)
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapActivity extends MapActivity {
	
    GPSservice gps;
	
	private static final String LATITUDE = "loc_lat";
	private static final String LONGITUDE = "loc_lon";

	// handle(r) for the database
    DatabaseHandler db = new DatabaseHandler(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        // new map view
        MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);	// zoom enable
	    
	    // list for map overlays
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    // use the launcher icon for gfx
	    Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher);

	    // new items overlay with our gfx
	    ItemsOverlay itemsoverlay = new ItemsOverlay(drawable, this);

	    //Log.d("Reading: ", "db");	// log some
	    
        // read locations from db
        List<DBObject> locations = db.getAllLocations();
        // close the database
        db.close();
        
	    //Log.d("Reading finished: ", "db");	// log some
	    
        // get intent (clicked item) if we came from the list view
	    Intent intent = getIntent();
	    // 2nd argument is default value for zooming position on the map
        int pos = intent.getIntExtra("pos", -1); 
        
	    //Log.d("Klik: ", pos + "");	// log some

        // point in map
        GeoPoint point;
        // address of point
        String address = "";
        
        // coordinates for clicked list item if available, else default center point
        float x = 60;
        float y = 24;
        
        // helpers for the loop
        float lat = 0;
        float lng = 0;

        // go thru locations we got from db
        for (DBObject cn : locations) {

        	// get lat/lon from location
        	lat = cn.getLatitude();
        	lng = cn.getLongitude();
        	
        	// make a point (convert to geopoint)
        	point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
        	
        	// get address
        	address = getAddressFromLocation((double)lat, (double)lng, this);
        	
    	    // add overlay with points address
    	    OverlayItem overlayitem = new OverlayItem(point, lat + " / " + lng, address);
    	    itemsoverlay.addOverlay(overlayitem);
    	    
            // debugging
        	//Log.d("Location: ", "Id: "+cn.getID()+" ,LAT: " + cn.getLatitude() + " ,LON: " + cn.getLongitude() + " ,DATE: " + cn.getDate());
        	
        	// get geopoint-coords for possible clicked item in list activity
        	if (cn.getID() == pos) {
        		x = lat;
        		y = lng;
        		//Log.d("Reading: ", pos + " " + x + " " + y);	// log some
        	}
        	
        }

	    //Log.d("Loop: ", "DONE");	// log some
		
        // add overlays to map 
	    mapOverlays.add(itemsoverlay);
	    
        //get the MapController object
        MapController controller = mapView.getController();

        //animate to the desired point
        controller.animateTo(new GeoPoint((int) (x * 1E6), (int) (y * 1E6)));

        //set the map zoom if we came from the list, 1 is top world view
        if (pos > 0) {
        	controller.setZoom(7);
    	    //Log.d("CLiCKED: " + pos, x + ":" + y);	// log some
        }

        //invalidate the map in order to show changes
        mapView.invalidate();
        
    }

    // we come here if user clicks the button to go back to the list activity
    public void onClick(View v) {
        switch(v.getId()) {
           case R.id.MapPrevButton:
        	   // show main menu
        	   Intent list = new Intent(MyMapActivity.this, MyListActivity.class);
               startActivity(list);
        	   break;
        }
    }

    
   
    // Method to get address from latitude and longitude
    public static String getAddressFromLocation(double latitude, double longitude, Context context) {
    	
        String locationAddress = "[address not found]";

        // check validity of coordinates
        if ((latitude > 90) || (latitude < -90) || (longitude > 180) || (longitude < -180)) {
        	return locationAddress;
        }
        
        // new geocoder with default locale
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        
        try {

        	// get just 1 address for the location (there might be more, but we only need one, and it's quicker)
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            // if we got an address (not null and list not empty)
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0); // get first address
                StringBuilder strReturnedAddress = new StringBuilder("Address:"); // new stringbuilder with a placeholder text
                // we add all lines from the address together, separated by newlines
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                locationAddress = strReturnedAddress.toString();

                locationAddress = locationAddress.replace("Address:", ""); // get rid of placeholder text
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return locationAddress;
        
    }

    
    // MENU creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    // MENU click reactions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text:     Toast.makeText(this, "(:<=- made by JuhoAP -=>:)", Toast.LENGTH_LONG).show();
                                break;
        }
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "gps-location" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		
    		// Get location data included in the Intent
    		double latitude = intent.getDoubleExtra(LATITUDE, 0);
    		double longitude = intent.getDoubleExtra(LONGITUDE, 0);
    		//Log.d("receiver", "Got latitude: " + latitude);
    		//Log.d("receiver", "Got longitude: " + longitude);
    		
    		// get date
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		String date = sdf.format(new Date());

    		// write to database
   			db.addLocation(new DBObject((float)latitude, (float)longitude, date ));
   	        db.close();
   	        
    		//Log.d("Location: ", latitude + " / " + longitude + " " + date);	// log it
    		
   	        // update map??
    		
    	}
    };
    
    @Override
    public void onResume() {
    	super.onResume();
        gps = new GPSservice(this);
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "gps-location".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("gps-location"));
	}
    
    @Override
    public void onPause(){
    	// unregister listener
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	// stop gps
    	gps.stopUsingGPS();
    	super.onPause();
    }

    @Override
    public void onDestroy() {
    	// unregister listener
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	// stop gps
        gps.stopUsingGPS();
        super.onDestroy();
    }
}
