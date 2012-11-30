package fi.juhoap.geotrack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyListActivity extends ListActivity {
	
    GPSservice gps;
	
	private static final String LATITUDE = "loc_lat";
	private static final String LONGITUDE = "loc_lon";
	
    //private final String FILENAME = "location_file";
	
    // list for graphical list row items
	final List<MyListRow> listOfRow = new ArrayList<MyListRow>();
	MyListAdapter adapter;
	
	// handle(r) for the database
    DatabaseHandler db = new DatabaseHandler(this);

    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setClickable(true);

        // read locations from db
        Log.d("Reading: ", "Reading all contacts..");
        List<DBObject> locations = db.getAllLocations();       
        db.close();
  
        // go thru locations we got from db
        for (DBObject cn : locations) {
            // add location data to list row
        	listOfRow.add(new MyListRow(cn.getLatitude(),cn.getLongitude(), cn.getDate()));
            // debugging
        	Log.d("Location: ", "Id: "+cn.getID()+" ,LAT: " + cn.getLatitude() + " ,LON: " + cn.getLongitude() + " ,DATE: " + cn.getDate());
        }

        // give the locations in list to adapter
        adapter = new MyListAdapter(this, listOfRow);

        // set click-listener
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
                //showToast(listOfRow.get(position).toString());

            	// create Intent for sending clicked position to map activity 
            	Intent map = new Intent(getApplicationContext(), MyMapActivity.class);
            	// put values to intent which will get in the map activity
            	// +1 because db id starts from 1, and position from 0
            	map.putExtra("pos", position+1);
            	// change activity to map view
                startActivity(map);
            }
        });

        list.setAdapter(adapter);
	}
	
    /*
	// n‰ytet‰‰n v‰h‰n leip‰‰ ;)
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    */
  
    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "gps-location" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		
    		// Get location data included in the Intent
    		double latitude = intent.getDoubleExtra(LATITUDE, 0);
    		double longitude = intent.getDoubleExtra(LONGITUDE, 0);
    		Log.d("receiver", "Got latitude: " + latitude);
    		Log.d("receiver", "Got longitude: " + longitude);
    		
    		// update ListView
    		//points.addGPSPoint(new GPSPoint((float)latitude, (float)longitude));
			/*
    		// save
     	   	String string = String.format("%f %f\n", latitude, longitude);
            try {
         	   FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
         	   fos.write(string.getBytes());
         	   fos.close();
         	   Log.d("WRITE list", "SUCCESS!");
            } catch (IOException e) {
         	   Log.d("WRITE list", "FAIL", e);
            }
            */
    		
    		// get date
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		String date = sdf.format(new Date());

    		// write to database
   			db.addLocation(new DBObject((float)latitude, (float)longitude, date ));
   	        db.close();
   	        
    		Log.d("Location: ", latitude + " / " + longitude + " " + date);	// log it
    		
    		listOfRow.add(new MyListRow((float)latitude, (float)longitude, date));
    		adapter.notifyDataSetChanged();
    		
    	}
    };
    
    
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
