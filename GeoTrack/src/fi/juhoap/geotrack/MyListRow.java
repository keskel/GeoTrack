package fi.juhoap.geotrack;

/**
  * row items in list
  *
  */

public class MyListRow {

	private float latitude;
	private float longitude;
	private String date;
	
	public MyListRow() {
		super();
	}
	
	public MyListRow(float lat, float lon, String dat) {
		super();
		this.latitude = lat;
		this.longitude = lon;
		this.date = dat;
	}
	
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    
    public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    
    public String toString() {
    	return latitude + " / " + longitude + " " + date;
    }

}
