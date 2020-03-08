package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Track;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailwayStationBusinessLogicException;
import org.camunda.bpm.unittest.departtrain.util.RailTestUtil;

import lombok.Data;

@Data
public class StationData {

	private List<Track> tracks = new ArrayList<Track>();

	public boolean allWaggonsPresent(List<String> waggonNumbers) {
		HashMap<String, Waggon> allWaggons = RailTestUtil.hashWaggons(getAllWaggons());
		for (String waggonNumber : waggonNumbers) {
			if (allWaggons.get(waggonNumber) == null) {
				return false;		
			}
		}
		return true;
	}

	public List<Waggon> getAllWaggons() {
		List<Waggon> result = new ArrayList<Waggon>();
		for (Track track : tracks) {
			if (track.getWaggons() != null) {
				result.addAll(track.getWaggons());				
			}
		}
		return result;
	}

	public void removeWaggon(String waggonNumber) {
		for (Track track : tracks) {
			track.removeWaggon(waggonNumber);
		}
	}

	public void setDefectCode(String waggonNumber, WaggonErrorCode waggonErrorCode) {
		findWaggon(waggonNumber).setWaggonErrorCode(waggonErrorCode);
	}

	private Waggon findWaggon(String waggonNumber) {
		Waggon waggon = null;
		for (Track track : tracks) {
			waggon = track.getWaggon(waggonNumber);
			if (waggon != null) {
				return waggon;
			}
		}
		return null;
	}

	public void addTrack(String trackNumber) {
		Track track = new Track();
		track.setTrackNumber(trackNumber);
		tracks.add(track);
	}

	public void addWaggons(String trackNumber, String[] waggonNumbers) {
		List<Waggon> waggons = new ArrayList<Waggon>();
		Waggon waggon = null;
		for (String waggonNumber : waggonNumbers) {
			waggon = new Waggon();
			waggon.setWaggonNumber(waggonNumber);
			waggons.add(waggon);
		}
		Track findTrack = findTrack(trackNumber);
		if (findTrack == null) {
			throw new RailwayStationBusinessLogicException("track " + trackNumber + " not present!!");
		}
		findTrack.setWaggons(waggons);
	}

	public Track findTrack(String trackNumber) {
		HashMap<String, Track> hashedTracks = RailTestUtil.hashTracks(tracks);
		return hashedTracks.get(trackNumber);
	}

	public boolean isWaggonCritical(String waggonNumber) {
		WaggonErrorCode waggonErrorCode = findWaggon(waggonNumber).getWaggonErrorCode();
		if (waggonErrorCode == null) {
			return false;
		}
		return waggonErrorCode.isCritical();
	}

	public void reset() {
		tracks = new ArrayList<Track>();
	}
}