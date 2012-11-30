package fi.juhoap.geotrack;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "geoTrackDB";

	// Locations table name
	private static final String TABLE_LOCATIONS = "locations";

	// Locations Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_LAT = "latitude";
	private static final String KEY_LON = "longitude";
	private static final String KEY_DATE = "date";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_LAT + " REAL,"
				+ KEY_LON + " REAL," + KEY_DATE + " TEXT" + ")";
		db.execSQL(CREATE_LOCATIONS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new location
	void addLocation(DBObject dbobject) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LAT, dbobject.getLatitude());
		values.put(KEY_LON, dbobject.getLongitude());
		values.put(KEY_DATE, dbobject.getDate());

		// Inserting Row
		db.insert(TABLE_LOCATIONS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single location
	DBObject getLocation(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_LOCATIONS, new String[] { KEY_ID,
				KEY_LAT, KEY_LON, KEY_DATE }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		DBObject location = new DBObject(Integer.parseInt(cursor.getString(0)),
				cursor.getFloat(1), cursor.getFloat(2), cursor.getString(3));
		// return location
		return location;
	}

	// Getting All Locations
	public List<DBObject> getAllLocations() {
		List<DBObject> locationList = new ArrayList<DBObject>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				DBObject location = new DBObject();
				location.setID(Integer.parseInt(cursor.getString(0)));
				location.setLatitude(cursor.getFloat(1));
				location.setLongitude(cursor.getFloat(2));
				location.setDate(cursor.getString(3));
				// Adding location to list
				locationList.add(location);
			} while (cursor.moveToNext());
		}

		// return location list
		return locationList;
	}

	// Updating single location
	public int updateLocation(DBObject location) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LAT, location.getLatitude());
		values.put(KEY_DATE, location.getLongitude());
		values.put(KEY_DATE, location.getDate());

		// updating row
		return db.update(TABLE_LOCATIONS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(location.getID()) });
	}

	// Deleting single location
	public void deleteLocation(DBObject location) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LOCATIONS, KEY_ID + " = ?",
				new String[] { String.valueOf(location.getID()) });
		db.close();
	}

	// Getting locations Count
	public int getLocationsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

}
