package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RailwayStationBusinessConfig {

	private HashMap<String, List<String>> tracksAndWaggons = new HashMap<String, List<String>>();

	public RailwayStationBusinessConfig withTracks(String... tracks) {
		for (String track : tracks) {
			tracksAndWaggons.put(track, new ArrayList<String>());
		}
		return this;
	}

	public RailwayStationBusinessConfig withWaggons(String track, String... waggons) {
		if (tracksAndWaggons.get(track) == null) {
			throw new RailwayStationBusinessConfigException("track is not existent --> cannot add waggons to it!!");
		}
		List<String> waggonList = new ArrayList<String>();
		for (String waggon : waggons) {
			waggonList.add(waggon);
		}
		tracksAndWaggons.put(track, waggonList);
		return this;
	}
}