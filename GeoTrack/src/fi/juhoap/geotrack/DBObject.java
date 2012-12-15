package fi.juhoap.geotrack;

/**
 * class for single database object
 */

public class DBObject {

	//private variables
	int id;
	float latitude;
	float longitude;
	String date;

	// Empty constructor
	public DBObject(){
	}

	// constructor
	public DBObject(int id, float latitude, float longitude, String date) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
	}

	// constructor without id
	public DBObject(float latitude, float longitude, String date){
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
	}
	
	// getting ID
	public int getID(){
		return this.id;
	}

	// setting id
	public void setID(int id){
		this.id = id;
	}

	// getting latitude
	public float getLatitude() {
		return this.latitude;
	}

	// setting latitude
	public void setLatitude(float latitude){
		this.latitude = latitude;
	}

	// getting longitude
	public float getLongitude() {
		return this.longitude;
	}

	// setting longitude
	public void setLongitude(float longitude){
		this.longitude = longitude;
	}
	
	// getting date
	public String getDate(){
		return this.date;
	}

	// setting date
	public void setDate(String date){
		this.date = date;
	}
}

