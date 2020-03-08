package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.unittest.delegate.departtrain.util.RailTestUtil;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Track;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;

import lombok.Data;

@Data
public class TracksAndWaggons {

	private HashMap<Track, List<Waggon>> data = new HashMap<Track, List<Waggon>>();

	public boolean hasTrack(Track track) {
		return data.keySet().contains(track);
	}

	public List<Waggon> getWaggons(String trackNumber) {
		return data.get(findTrack(trackNumber));
	}

	public void putTrack(String trackNumber) {
		Track track = new Track();
		track.setTrackNumber(trackNumber);
		data.put(track, new ArrayList<Waggon>());
	}

	public Track getTrack(Track track) {
		if (!(data.keySet().contains(track))) {
			return null;	
		}
		for (Track t : data.keySet()) {
			
		}
		return null;
	}
	
	private Track findTrack(String trackNumber) {
		for (Track track : data.keySet()) {
			if (track.getTrackNumber().equals(trackNumber)) {
				return track;
			}
		}
		return null;
	}

	public List<String> getWaggonNumbers(String trackNumber) {
		List<String> result = new ArrayList<String>();
		for (Waggon waggon : data.get(findTrack(trackNumber))) {
			result.add(waggon.getWaggonNumber());
		}
		return result;
	}

	public Set<Track> getTracks() {
		return data.keySet();
	}

	public void putWaggons(String trackNumber, String[] waggonNumbers) {
		Track track = findTrack(trackNumber);
		if (track == null) {
			throw new RailwayStationBusinessLogicException("unable to find track with track number: " + trackNumber);
		}
		List<Waggon> waggons = new ArrayList<Waggon>();
		for (String waggonNumber : waggonNumbers) {
			waggons.add(convertToWaggon(waggonNumber));
		}
		data.put(track, waggons);
	}

	private Waggon convertToWaggon(String waggonNumber) {
		Waggon waggon = new Waggon();
		waggon.setWaggonNumber(waggonNumber);
		return waggon;
	}

	public HashMap<String, Waggon> getAllWaggons() {
		HashMap<String, Waggon> result = new HashMap<String, Waggon>();
		for (Track track : data.keySet()) {
			for (Waggon waggon : data.get(track)) {
				result.put(waggon.getWaggonNumber(), waggon);				
			}
		}
		return result;
	}

	public void removeWaggon(String waggonNumber) {
		for (Track track : data.keySet()) {
			if (data.get(track) != null) {
				HashMap<String, Waggon> trackWaggons = RailTestUtil.hashWaggons(data.get(track));
				if (trackWaggons.get(waggonNumber) != null) {
					// waggon is on that track
					trackWaggons.remove(waggonNumber);
					data.put(track, new ArrayList<Waggon>(trackWaggons.values()));
				}
			}
		}
	}
}