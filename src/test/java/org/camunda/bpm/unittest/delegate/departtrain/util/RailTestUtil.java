package org.camunda.bpm.unittest.delegate.departtrain.util;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;

public class RailTestUtil {

	public static HashMap<String, Waggon> hashWaggons(List<Waggon> waggons) {
		HashMap<String, Waggon> hashedWaggons = new HashMap<String, Waggon>();
		for (Waggon waggon : waggons) {
			hashedWaggons.put(waggon.getWaggonNumber(), waggon);
		}
		return hashedWaggons; 
	}
}