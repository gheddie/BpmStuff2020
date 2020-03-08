package org.camunda.bpm.unittest.departtrain.util;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Track;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;

public class RailTestUtil {

	public static HashMap<String, Waggon> hashWaggons(List<Waggon> waggons) {
		HashMap<String, Waggon> hashedWaggons = new HashMap<String, Waggon>();
		if (waggons == null) {
			return hashedWaggons;
		}
		for (Waggon waggon : waggons) {
			hashedWaggons.put(waggon.getWaggonNumber(), waggon);
		}
		return hashedWaggons; 
	}

	public static HashMap<String, Track> hashTracks(List<Track> tracks) {
		HashMap<String, Track> hashedTracks = new HashMap<String, Track>();
		for (Track track : tracks) {
			hashedTracks.put(track.getTrackNumber(), track);
		}
		return hashedTracks;
	}
}