package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Polygon;

public class Http {
	
	private static final HttpClient client = HttpClient.newHttpClient();
	public static String readWeb(String urlString) throws IOException, InterruptedException {
		
		var request = HttpRequest.newBuilder()
				.uri(URI.create(urlString))
				.build();
		// The response object is of class HttpResponse<String> 
		var response = client.send(request, BodyHandlers.ofString());
		return response.body();
	}
	// Deserialise Json record to a java object  
	public static ArrayList<Sensors> parsingMap(String[] args) throws IOException, InterruptedException {
		String urlString = "http://localhost:" + args[6] + "/maps/" + args[2] + "/" + args[1] + "/"
				+ args[0] + "/air-quality-data.json";
		Type listType = new TypeToken<ArrayList<Sensors>>() {}.getType();
		String jsonListString = readWeb(urlString);
		ArrayList<Sensors> sensorList = new Gson().fromJson(jsonListString, listType);
		
		return sensorList;
	}
	// Deserialise Json record to a java object
	public static ArrayList<What3Words> parsingWords(String[] args) throws IOException, InterruptedException {

		ArrayList<Sensors> sensorList = parsingMap(args);
		ArrayList<What3Words> words = new ArrayList<What3Words>();
		for(Sensors s : sensorList) {
			String a = s.getLocation();
			String[] substrings = a.split("\\.");
			String wordsString = "http://localhost:" + args[6] + "/words/" + substrings[0] + "/" + substrings[1] + "/"
					+ substrings[2] + "/details.json";
			String jsonDetailString = readWeb(wordsString);
			var detailw3w = new Gson().fromJson(jsonDetailString, What3Words.class);
			words.add(detailw3w);
		}
		return words;
	}
	// Deserialise Json record to a java object
	public static List<Polygon> parsingZone(String[] args) throws IOException, InterruptedException {
		String urlString = "http://localhost:" + args[6] + "/buildings/no-fly-zones.geojson";
		String ZoneString = readWeb(urlString);
		var noFlyZoneList = FeatureCollection.fromJson(ZoneString).features();
		List<Polygon> gList = new ArrayList<Polygon>();
		for(Feature f : noFlyZoneList) {
			Geometry g = f.geometry();
			gList.add((Polygon)g);
		}
		return gList;
	}

}
