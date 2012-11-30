package fi.juhoap.geotrack;

/** 
 * MAP
 * (sometimes doesn't get data from map server and takes a while to load)
 * - k��nt�ess� "puhelimen" n�ytt�� lataa osoitteet uudestaan.. ei viel� l�ytynyt ratkaisua
 * 
 */

// TODO: k�nnyn k��nt� lataa osotteetki uusiks! korjaa?

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
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

	// handle(r) for the database
    DatabaseHandler db = new DatabaseHandler(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);	// zoom enable
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher);

	    ItemsOverlay itemsoverlay = new ItemsOverlay(drawable, this);

	    Log.d("Reading: ", "db");	// log some
	    
        // read locations from db
        List<DBObject> locations = db.getAllLocations();
        
	    Log.d("Reading finished: ", "db");	// log some
	    
        // get intent (clicked item) if we came from the list view
	    Intent intent = getIntent();
	    // 2nd argument is default value for zooming position on the map
        int pos = intent.getIntExtra("pos", -1); 
        
	    Log.d("Klik: ", pos + "");	// log some

        // close the base
        db.close();
        
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
        	
        	// make a point
        	point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
        	
        	// get address
        	address = getAddressFromLocation((double)lat, (double)lng, this);
        	
    	    // add overlay with points address
    	    OverlayItem overlayitem = new OverlayItem(point, lat + " / " + lng, address);
    	    itemsoverlay.addOverlay(overlayitem);
    	    
            // debugging
        	Log.d("Location: ", "Id: "+cn.getID()+" ,LAT: " + cn.getLatitude() + " ,LON: " + cn.getLongitude() + " ,DATE: " + cn.getDate());
        	
        	// get geopoint-coords for clicked in list activity
        	if (cn.getID() == pos) {
        		x = lat;
        		y = lng;
        		Log.d("Reading: ", pos + " " + x + " " + y);	// log some
        	}
        	
        }

	    Log.d("Loop: ", "DONE");	// log some
		
        // add overlays to map 
	    mapOverlays.add(itemsoverlay);
	    
        //get the MapController object
        MapController controller = mapView.getController();

        //animate to the desired point
        controller.animateTo(new GeoPoint((int) (x * 1E6), (int) (y * 1E6)));

        //set the map zoom if we came from the list, 1 is top world view
        if (pos > 0) {
        	controller.setZoom(7);
    	    Log.d("CLiCKED: " + pos, x + ":" + y);	// log some
        }

        //invalidate the map in order to show changes
        mapView.invalidate();
        
    }

    public void onClick(View v) {
        switch(v.getId()) {
           case R.id.MapPrevButton:
        	   // n�yt� p��menu
        	   Intent list = new Intent(MyMapActivity.this, MyListActivity.class);
               startActivity(list);
        	   break;
        }
    }

    
   
    // Method to getAddress from particular latitude and longitude
    // TODO: comment
    public static String getAddressFromLocation(double latitude, double longitude, Context context) {
    	
        String locationAddress = "[address not found]";

        // check validity of coordinates
        if ((latitude > 90) || (latitude < -90) || (longitude > 180) || (longitude < -180)) {
        	return locationAddress;
        }
        
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                locationAddress = strReturnedAddress.toString();

                locationAddress = locationAddress.replace("Address:", "");
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
}
