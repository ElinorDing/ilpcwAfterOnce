package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Routes {
	
	String startLng;
	String startLat;
	Drone drone;
	Point init;
	ArrayList<What3Words> sensors_location;
	NoFlyZone noFlyZone;
	
	public Routes(String startLng, String startLat, ArrayList<What3Words> sensors_location, 
			NoFlyZone noFlyZone) {
		init = Point.fromLngLat(Double.parseDouble(startLng), Double.parseDouble(startLat));
		drone = new Drone(Double.parseDouble(startLng), Double.parseDouble(startLat));
		this.sensors_location = sensors_location;
		this.noFlyZone = noFlyZone;
//		this.close_points = close_points;
	}
	/*
	 * Calculate the direction of two points and convert it to 0-2pi range.
	 */
	public double findDirection(Drone drone, Point next) {
		double xDiff = next.longitude()-drone.lng;
        double yDiff = next.latitude() - drone.lat;
        double angleVal = 0;
        if(yDiff >0) angleVal = Math.toDegrees(Math.atan2(yDiff, xDiff));
        if(yDiff <0) angleVal = 360 - (-Math.toDegrees(Math.atan2(yDiff, xDiff)))%360;
		return angleVal;
	}
	/*
	 * Find the test next point to check whether the drone can move to or not
	 */
	public Point findNextPoint(Drone drone, double angle) {
		double xDiff = drone.lng+Math.cos(angle*(Math.PI/180)) * 0.0003;
		double yDiff = drone.lat+Math.sin(angle*(Math.PI/180)) * 0.0003;
		Point test = Point.fromLngLat(xDiff, yDiff);		
		return test;
	}
	/*
	 * Return a triplet of list of directions, move step between two point,
	 * and each moving point.
	 */
	public Triplet<ArrayList<Integer>, Integer, ArrayList<Point>> adjust_arrival(Point next) {
		int count = 0;
		ArrayList<Integer> all_direction = new ArrayList<Integer>();
		ArrayList<Point> all_point = new ArrayList<Point>();
		var direction = findDirection(drone, next);
		Point test_point = null;
		int multi = 0;
		int times = 1;
		while(drone.getDis(drone.getPoint(),next) > 0.0002) {
			while(direction %10 != 0) {
				direction = direction + (0-direction%10);
				test_point = findNextPoint(drone, direction);
				if(noFlyZone.isClear(drone, test_point)) {
					direction = (direction + Math.pow(-1, multi)*-1*times*10)%360;
				}
			}
			test_point = findNextPoint(drone, direction);
			while(noFlyZone.isClear(drone, test_point) || all_point.contains(test_point)) {
				multi++;
				times++;
				direction  = (direction + Math.pow(-1, multi)*-1*times*10)%360;
				test_point = findNextPoint(drone, direction);
			}
			multi = 0;
			times = 1;
			count++;
			all_direction.add((int)direction);
			all_point.add(test_point);
			drone.lng = test_point.longitude();
			drone.lat = test_point.latitude();
			direction = findDirection(drone, next);
		}
		return Triplet.with(all_direction, count, all_point);
	}
	/*
	 * Return 
	 */
	ArrayList<Integer> close_points = new ArrayList<Integer>();
	public Triplet<ArrayList<Point>, Integer, ArrayList<Integer>> allSteps(ArrayList<Point> route_order) {
		ArrayList<Point> allPoint = new ArrayList<Point>();
		ArrayList<Integer> all_direction = new ArrayList<Integer>();
		int move_step = 0;
		allPoint.add(route_order.get(0));
		Point current = route_order.get(0);
		for(int i = 0; i< route_order.size()-1; i++) {
			Point next = route_order.get(i+1);
			var aa = adjust_arrival(next);
			ArrayList<Point> steps_bew = aa.getValue2();
			close_points.add(aa.getValue1());
			allPoint.addAll(steps_bew);
			//allPoint.add(next);
			current = steps_bew.get(steps_bew.size()-1);

			move_step += aa.getValue1();
			all_direction.addAll(aa.getValue0());
		}
		return Triplet.with(allPoint, move_step, all_direction);	
	}
	public ArrayList<Integer> getClose(){
		return close_points;
	}
	
	public Triplet<ArrayList<Point>, Integer, ArrayList<Integer>> beyondLimit(Point start_point) throws IOException, InterruptedException {

		Triplet<ArrayList<Point>, Integer, ArrayList<Integer>> getRoute = null;
		ArrayList<Point> sensors_route = drone.Greedy(start_point, sensors_location);
		for(int i = 0; i< Double.POSITIVE_INFINITY;i++) {
			getRoute = allSteps(sensors_route);
			if (getRoute.getValue1() > 150) {
				Random random_pick = new Random();
				int index = random_pick.nextInt(sensors_location.size());
				sensors_location.remove(index);
			}
			else break;
		}
		System.out.println(getRoute.getValue1());
		return getRoute;
	}
	
	

}
