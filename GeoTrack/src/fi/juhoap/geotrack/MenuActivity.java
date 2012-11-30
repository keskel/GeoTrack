package fi.juhoap.geotrack;

/** class for the main menu */

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends FragmentActivity {

    GPSservice gps;
	
	private double latitude;
    private double longitude;
	private static final String LATITUDE = "loc_lat";
	private static final String LONGITUDE = "loc_lon";
	
    //private final String FILENAME = "location_file";
    
	// handle(r) for the database
    DatabaseHandler db = new DatabaseHandler(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "gps-location" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// Get location data included in the Intent
    		latitude = intent.getDoubleExtra(LATITUDE, 0);
    		longitude = intent.getDoubleExtra(LONGITUDE, 0);
    		Log.d("receiver", "Got latitude: " + latitude);
    		Log.d("receiver", "Got longitude: " + longitude);

    		/*
    		String string = String.format("%f %f\n", latitude, longitude);
    		// save (we shouldn't save at every update.. but this is before i do it more wisely)
    		try {
    			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
    			fos.write(string.getBytes());
    			fos.close();
    			Log.d("WRITE menu", "SUCCESS!");
    		} catch (IOException e) {
    			Log.d("WRITE menu", "FAIL", e);
    		}
    		*/
    		
    		// get date
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		String date = sdf.format(new Date());

    		// write to database
   			db.addLocation(new DBObject((float)latitude, (float)longitude, date ));
   	        db.close();
   	        
    		Log.d("Location: ", latitude + " / " + longitude + " " + date);	// log it
    		
    	}
    };
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
    
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
    	super.onPause();
    	// unregister listener
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	// stop gps
    	gps.stopUsingGPS();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    	// unregister listener
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	// stop gps
        gps.stopUsingGPS();
    }
    
    public void onClick(View v) {
        
        switch(v.getId()) {
           case R.id.button2:
        	   // save
        	   
        	   // näytä lista
        	   Intent list = new Intent(MenuActivity.this, MyListActivity.class);
               startActivity(list);
        	   break;
           case R.id.button1:
        	   // save
        	   
        	   // näytä kartta
        	   Intent map = new Intent(MenuActivity.this, MyMapActivity.class);
               startActivity(map);
        	   break;
        }
       
    }
    
}
