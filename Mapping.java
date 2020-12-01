package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.ArrayList;

import org.javatuples.Pair;

import com.mapbox.geojson.*;;

public class Mapping {
	
	ArrayList<Sensors> sensor_order;
	Routes routes;
	
	public Mapping(Routes routes, ArrayList<Sensors> sensor_order) {
		this.routes = routes;
		this.sensor_order = sensor_order;
	}
	/*
	 * Set the corresponding point to each sensor as an attribute
	 */
	public void setLoc() {
		for(int i =0;i<sensor_order.size();i++) {
			Point b = routes.sensors_location.get(i).coordinates.getPoint();
			sensor_order.get(i).setWhere(b);
		}
	}
	/*
	 * Plot the route line and add read each sensor
	 * when every time the drone moves a step
	 */
	public Pair<GeoJson, ArrayList<String>> getInfo(String[] args) throws IOException, InterruptedException {
		
		// The drone starts at the starting point
		Drone drone = new Drone(Double.parseDouble(args[4]), Double.parseDouble(args[3]));
		var all_route = routes.beyondLimit(drone.getPoint());
		var route= all_route.getValue0();
		setLoc();
		System.out.println(route);
		
		// Convert list of points to lineString
		var fl = new ArrayList<Feature>();
		LineString fly_route = LineString.fromLngLats(route);
		var routeLine = Feature.fromGeometry((Geometry)fly_route);
		routeLine.addStringProperty("fill", "#000000");
		fl.add(routeLine);
		
		// keep all locations on track
		var all_location = new ArrayList<String>();
		
		// find the readable sensor once the next point is within the range once the drone made a move.
		for(int i = 0; i<route.size()-1;i++) {
			for(int j = 0; j <sensor_order.size();j++) {
				System.out.println("i" +" "+ i);
				Point matching = routes.sensors_location.get(j).coordinates.getPoint(); 
				drone.lng = route.get(i+1).longitude();
				drone.lat = route.get(i+1).latitude();
				var matched = sensor_order.get(j);
				
				if(drone.getDis(drone.getPoint(), matched.getWhere())<=0.0002) {
					FillSensors one = new FillSensors(matched.getReading(), matched.getBattery()); 
					Feature f = Feature.fromGeometry((Geometry)matched.getWhere());
					f = one.fillInfo(f);
					f.addStringProperty("location", matched.getLocation());
					fl.add(f);
					all_location.add(matched.getLocation());
				}
			}
			all_location.add(null);
		}
		System.out.println("..."+all_location.size());
		FeatureCollection fc = FeatureCollection.fromFeatures(fl);
		fc.toJson();
		return Pair.with(fc, all_location);
		
	}

}
