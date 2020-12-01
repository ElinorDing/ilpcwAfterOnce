package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class What3Words {
	private String country;
	private String words;
	private String language;
	private String maps;
	private String nearestPlace;
	
	public What3Words(String words, String country, String language, String maps, String nearestPlace) {
		words = this.words;
		country = this.country;
		language = this.language;
		maps = this.maps;
		nearestPlace = this.nearestPlace;
	}
	
	Location coordinates;
	public static class Location{
		double lng;
		double lat;
		public Point getPoint() {
			return Point.fromLngLat(lng, lat);
		}
	}
	
	public Location getCoordinates() {
		return coordinates;
	}
	public String getWords() {
		return words;
	}
	public String getMap() {
		return maps;
	}
	

}
