package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;


public class NoFlyZone {
	
	double lngLower = -3.192473;
	double lngUpper = -3.184319;
	double latLower = 55.942617;
	double latUpper = 55.946233;
	List<Polygon> noFlyZone;
	
	public NoFlyZone(List<Polygon> noFlyZone) {
		this.noFlyZone = noFlyZone;
	}
	
	/*
	 * Checking four points formed two lines intersect or not.
	 */
	public Boolean isCrossing(Point A, Point B, Point sp, Point ep) {
		double vertice1X = A.longitude();
		double vertice1Y = A.latitude();
		
		double vertice2X = B.longitude();
		double vertice2Y = B.latitude();
		
		double sp_x = sp.longitude();
		double sp_y = sp.latitude();
		
		double ep_x = ep.longitude();
		double ep_y = ep.latitude();
		
		
		Boolean isIntersect = Line2D.linesIntersect(vertice1X, vertice1Y, vertice2X, vertice2Y, 
													sp_x, sp_y, ep_x, ep_y);
		
		return isIntersect;
	}
	
	/*
	 * Check whether the next point is violated the requirement or not.
	 * True if violated, false otherwise.
	 */
	public Boolean isClear(Drone drone, Point test_point) {
		
		// Define four vertices of confinement zone.
		Point confinementVx1 = Point.fromLngLat(lngLower, latUpper);
		Point confinementVx2 = Point.fromLngLat(lngUpper, latUpper);
		Point confinementVx3 = Point.fromLngLat(lngUpper, latLower);
		Point confinementVx4 = Point.fromLngLat(lngLower, latLower);
		
		Point current = drone.getPoint();
		Boolean isInconfine1 = isCrossing(confinementVx1, confinementVx2, current, test_point);
		Boolean isInconfine2 = isCrossing(confinementVx2, confinementVx3, current, test_point);
		Boolean isInconfine3 = isCrossing(confinementVx3, confinementVx4, current, test_point);
		Boolean isInconfine4 = isCrossing(confinementVx4, confinementVx1, current, test_point);
		
		// Whether the next point is crossing lines of confinement zone and no-fly-zone or not.
		Boolean isViolated = isInconfine1 || isInconfine2 || isInconfine3 || isInconfine4;
		
		for(int one = 0; one < noFlyZone.size(); one++) {
			
			ArrayList<Point> each_zone = new ArrayList<Point>();	
			each_zone.addAll(noFlyZone.get(one).coordinates().get(0));
			int num_vex = each_zone.size();
			
			for(int i = 0; i < num_vex-1; i++) {				
				Point one_vertice = each_zone.get(i);
				Point another_vex = each_zone.get(i+1);
				isViolated = isViolated || isCrossing(one_vertice, another_vex, current, test_point);
			}
		}
		return isViolated;
	}

}
