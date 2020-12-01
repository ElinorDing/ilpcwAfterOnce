package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import com.mapbox.geojson.Point;

public class Drone {
	/*
	 * Set up the current point of the drone.
	 */
	double lng;
	double lat;
	
	public Drone(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}
	public Point getPoint() {
		return Point.fromLngLat(lng, lat);
	}
	/*
	 * Get the distance between of each two moving points,
	 * providing a readable range condition which will be used in later time.
	 */
	public double getDis(Point current, Point next) {
		
		double lngDiff = Math.pow((next.longitude()-current.longitude()), 2);
		double latDiff = Math.pow((next.latitude()-current.latitude()), 2);
		double tourDis = Math.sqrt(lngDiff+latDiff);
		return tourDis;
	}
	/*
	 * Find the expected move steps of two points.
	 */
	public int expectedMove(double distance) {
		int move_num = (int)Math.round(distance/0.0003);
		return move_num;
	}
	/*
	 * Find the expected moving steps of any pairs of sensors,
	 * return the list of points which always the drone goes to
	 * the closest sensor.
	 */
	public ArrayList<Point> Greedy(Point start_point, ArrayList<What3Words> sensors_location){
		
		ArrayList<Point> get_all_point = new ArrayList<Point>();
		// Get corresponding points of sensors_location
		get_all_point.add(start_point);	
		for(int i = 0; i<sensors_location.size(); i++) {
			get_all_point.add(sensors_location.get(i).getCoordinates().getPoint());
		}
		// Initialise the correct sensor points order based on Greedy algorithm.
		ArrayList<Point> route_order = new ArrayList<Point>();
		route_order.add(start_point);
		
		// Give all data of distance of all pair points. 
		int get_num = get_all_point.size();
		int[][] route_weight = new int[get_num][get_num];		
		for(int i =0; i< get_num; i++) {
			for(int j = 0; j< get_num; j++) {
				Point current = get_all_point.get(i);
				Point next = get_all_point.get(j);
				route_weight[i][j] = expectedMove(getDis(current, next)); 
			}
		}
		// Return the expected order of sensor points.
		int pointed= 0;
		int next_pointed = 0;
		int shortest = (int)Double.POSITIVE_INFINITY;
		for(int i = 0; i< get_num-1; i++) {
			for(int j = 0; j < get_num; j++) {
				if(route_weight[pointed][j] != 0 && route_weight[pointed][j] <= shortest && 
						!route_order.contains(get_all_point.get(j))) {
					shortest = route_weight[pointed][j];
					next_pointed = j;
				}	
			}
			pointed = next_pointed;
			route_order.add(get_all_point.get(pointed));
			shortest = (int)Double.POSITIVE_INFINITY;	
		}
		route_order.add(start_point);
		return route_order;
	}
	
	
	
}
