package fi.juhoap.geotrack;

// TODO: menu

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
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

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
        setContentView(R.layout.activity_splash);
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
			 * TODO: remove file lines
			// save
			String string = String.format("%f %f\n", latitude, longitude);
			try {
			   FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
			   fos.write(string.getBytes());
			   fos.close();
			   Log.d("WRITE main", "SUCCESS!");
			} catch (IOException e) {
			   Log.d("WRITE main", "FAIL", e);
			}
			*/
    		
    		// get date
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		String date = sdf.format(new Date());

    		// write to database
   			db.addLocation(new DBObject((float)latitude, (float)longitude, date ));
   	        db.close();
   	        
    		Log.d("Location: ", latitude + " / " + longitude + " " + date);	// log it

   			// change activity (screen) to menu activity
       		Intent menu = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(menu);
    	}
    };

// TODO: remove these commented lines when menu works
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
    
    // when we get back from anything, we start over.. the gps and broadcaster..
    // (i wonder if this is efficient or not..)
    @Override
    public void onResume() {
    	super.onResume();
        gps = new GPSservice(this);
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "gps-location".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("gps-location"));
    }
    
    // stop all (or should we not.. hmm?)
    @Override
    public void onPause() {
    	super.onPause();
    	// unregister listener
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	// stop gps
    	gps.stopUsingGPS();
    }

    // destroy! destoy!
    @Override
    public void onDestroy() {
        super.onDestroy();
    	// unregister listener
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	// stop gps
        gps.stopUsingGPS();
    }
    
}
