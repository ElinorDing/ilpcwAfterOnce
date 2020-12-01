package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.Point;

public class FileForm {
	
	static Routes aRoute;
	static ArrayList<Sensors> sensor_locations;
	public FileForm(Routes aRoute, ArrayList<Sensors> sensor_locations) {
		this.aRoute = aRoute;
		this.sensor_locations = sensor_locations;
	}
	
	public static void readingTxt(String[] args) throws IOException, InterruptedException {
		
    	try {
    		
			String fileRead = "readings-" + args[0] + "-" + args[1] + "-" + args[2]+".geojson";
			Mapping a = new Mapping(aRoute,sensor_locations);
			GeoJson aAqmap = a.getInfo(args).getValue0();
  	      	FileWriter aqMap = new FileWriter(fileRead);
  	      	aqMap.write(aAqmap.toJson());
  	      	aqMap.close();
  	      	System.out.println("The file is successfully created.");
  	      	
  	      	String fileFlight = "flightpath-"+args[0]+"-"+args[1]+"-"+args[2]+".txt";
  	      	FileWriter flightpath = new FileWriter(fileFlight);
  	      	
  	      	
  	      	var bL = aRoute.beyondLimit(Point.fromLngLat(Double.parseDouble(args[4]), 
  	      												Double.parseDouble(args[3])));
  	      	var getDetails = bL.getValue0();
  	      	var total_move = bL.getValue1();
  	      	var all_direction = bL.getValue2();
  	      	String newLine = System.getProperty("line.separator");
//  	      	System.out.println("..."+total_move);
  	      	
  	      	for(int i = 0; i< total_move-1; i++) {
  	      		
//  	      		var sb = String.format("%d,%f,%f,%d,%f,%f,%s\n", 
//  	      				i,getDetails.get(i).longitude(),getDetails.get(i).latitude(),
//						all_direction.get(i),getDetails.get(i+1).longitude(),
//						getDetails.get(i+1).latitude(),a.getInfo(args).getValue1().get(i+1));
  	      		double curr_lng = getDetails.get(i).longitude();
  	      		double curr_lat = getDetails.get(i).latitude();
  	      		int direction = all_direction.get(i);
	      		double next_lng = getDetails.get(i+1).longitude();
	      		double next_lat = getDetails.get(i+1).latitude();
	      		String location = a.getInfo(args).getValue1().get(i);
	      		flightpath.write(i + "," + curr_lng + "," + curr_lat + "," + direction + "," + next_lng + 
	      			"," + next_lat + "," + location + newLine);
//  	      		flightpath.write(sb);
  	      	}
  	      	flightpath.close();
  	      	
  	      	System.out.println("Successfully wrote to the file.");
  	    } catch (IOException e) {
  	    	System.out.println("An error occurred.");
  	    	e.printStackTrace();
  	    }
	}

}
