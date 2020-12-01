package uk.ac.ed.inf.aqmaps;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.javatuples.Triplet;
import com.mapbox.geojson.*;


public class App 
{
	
	
	
    public static void main(String[] args) throws IOException, InterruptedException
    {
    	String[] input = {"02", "02", "2020", "55.9444", "-3.1878", "5678", "80"};
    	
    	var noFlyZoneList = Http.parsingZone(input);
//    	System.out.println(noFlyZoneList.get(0));
    	var sensorsList = Http.parsingMap(input);
//    	System.out.println(sensorsList.get(0).getLocation());
    	var w3wList = Http.parsingWords(input);
//    	System.out.println(w3wList);
    	
//    	Drone drone = new Drone(Double.parseDouble(input[4]), Double.parseDouble(input[3]));
    	NoFlyZone noFlyZone = new NoFlyZone(noFlyZoneList);
    	Routes aRoute = new Routes(input[4], input[3], w3wList, noFlyZone);
    	Mapping a = new Mapping(aRoute, sensorsList);
    	FileForm b = new FileForm(aRoute, sensorsList);
    	
    	b.readingTxt(input);
    	
    	    

    }
}
