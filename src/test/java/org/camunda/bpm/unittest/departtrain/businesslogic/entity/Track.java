package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.unittest.delegate.departtrain.util.RailTestUtil;

import lombok.Data;

@Data
public class Track {

	private String trackNumber;
	
	private List<Waggon> waggons;

	public List<String> getWaggonNumbers(boolean showWaggonDefects) {
		List<String> result = new ArrayList<String>();
		if (waggons == null) {
			return result;
		}
		for (Waggon waggon : waggons) {
			result.add(waggon.getWaggonNumber());
		}
		return result;
	}

	public void removeWaggon(String waggonNumber) {
		HashMap<String, Waggon> trackWaggons = RailTestUtil.hashWaggons(waggons);
		trackWaggons.remove(waggonNumber);
		waggons = new ArrayList<Waggon>(trackWaggons.values());
	}

	public Waggon getWaggon(String waggonNumber) {
		return RailTestUtil.hashWaggons(waggons).get(waggonNumber);
	}
}