package fi.juhoap.geotrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GPSPointsList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<GPSPoint>data;
	
	public GPSPointsList()
	{
		data = new ArrayList<GPSPoint>();
	}
	public GPSPoint getPoint(int i)
	{
		return data.get(i);
	}
	public void addGPSPoint(GPSPoint p)
	{
		data.add(p);
	}
	public List<GPSPoint> getData()
	{
		return data;
	}

}