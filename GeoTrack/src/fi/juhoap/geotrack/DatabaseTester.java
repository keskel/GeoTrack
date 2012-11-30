package fi.juhoap.geotrack;

	import java.util.List;

	import android.app.Activity;
	import android.os.Bundle;
	import android.util.Log;
	import android.widget.TextView;

	public class DatabaseTester extends Activity {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dbtest);

	        DatabaseHandler db = new DatabaseHandler(this);

	        /**
	         * CRUD Operations
	         * */
	        // Inserting Contacts
	        Log.d("Insert: ", "Inserting ..");
	        db.addLocation(new DBObject(123.00462f, -24.16240f, "22/11/2012 22:11:11"));
	        db.addLocation(new DBObject(-23.17002f, -123.00913f, "11/12/2012 12:21:12"));
	        db.addLocation(new DBObject(123.82169f, 23.84200f, "12/12/2012 12:12:21"));
	        db.addLocation(new DBObject(-123.43001f, -63.42600f, "21/12/2012 21:22:12"));
	        
	        // Reading all contacts
	        Log.d("Reading: ", "Reading all contacts..");
	        List<DBObject> locations = db.getAllLocations();       

	        for (DBObject cn : locations) {
	        	String log = "Id: "+cn.getID()+" ,LAT: " + cn.getLatitude() + " ,LON: " + cn.getLongitude() + " ,DATE: " + cn.getDate();
	                // Writing Contacts to log
	        	Log.d("Location: ", log);
	        }
	    }
	}
	
