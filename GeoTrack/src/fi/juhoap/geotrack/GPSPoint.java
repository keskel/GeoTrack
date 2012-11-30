package fi.juhoap.geotrack;

import java.io.Serializable;

public class GPSPoint implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private float x;
	private float y;
	
	public GPSPoint(float x,float y)
	{
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public String toString() {
		return this.x + " " + this.y;
	}
	
}