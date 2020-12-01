package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Feature;

public class FillSensors {
	String Reading;
	double battery;
	public FillSensors(String reading, double battery) {
		this.Reading = reading;
		this.battery = battery;
		
	}
	public Feature fillInfo(Feature a_feature) {
		String s =  "rgb-string";
		a_feature.addStringProperty("marker-size", "medium");
		if(battery <= 10) {
			a_feature.addStringProperty("marker-symbol" , "cross");
			a_feature.addStringProperty("marker-color", "#000000");
			a_feature.addStringProperty( s, "#000000");		
		}
		else {
			double num = Double.parseDouble(Reading);
			if(num>= 0 && num <128) {
				a_feature.addStringProperty("marker-symbol" , "lighthouse");
				if(num >= 0 && num < 32) {
					a_feature.addStringProperty("Marker symbol" , "cross");
					a_feature.addStringProperty( s, "#00ff00");
					a_feature.addStringProperty("marker-color", "#00ff00");
				}
				if(num >= 32 && num < 64){		
					a_feature.addStringProperty( s, "#40ff00");
					a_feature.addStringProperty("marker-color", "#40ff00");
				}
				if(num >= 64 && num < 96){			
					a_feature.addStringProperty( s, "#80ff00");
					a_feature.addStringProperty("marker-color", "#80ff00");
				}
				if(num >= 96 && num < 128) {
					a_feature.addStringProperty( s, "#c0ff00");
					a_feature.addStringProperty("marker-color", "#c0ff00");
				}
			}
			if(num>= 128 && num <256) {
				a_feature.addStringProperty("marker-symbol" , "danger");
				if(num >= 128 && num < 160) {
					a_feature.addStringProperty( s, "#ffc000");
					a_feature.addStringProperty("marker-color", "#ffc000");
				}
				if(num >= 160 && num < 192) {				
					a_feature.addStringProperty( s, "#ff8000");
					a_feature.addStringProperty("marker-color", "#ff8000");
				}
				if(num >= 192 && num < 224){			
					a_feature.addStringProperty( s, "#ff4000");
					a_feature.addStringProperty("marker-color", "#ff4000");
				}
				if(num >= 224 && num < 256) {
					a_feature.addStringProperty( s, "#ff0000");
					a_feature.addStringProperty("marker-color", "#ff0000");
				}
			}
		}
		return a_feature;
		
	}

}
