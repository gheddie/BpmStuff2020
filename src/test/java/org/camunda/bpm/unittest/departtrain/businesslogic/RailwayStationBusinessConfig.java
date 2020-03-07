package org.camunda.bpm.unittest.departtrain.businesslogic;

import lombok.Data;

@Data
public class RailwayStationBusinessConfig {

	private TracksAndWaggons tracksAndWaggons = new TracksAndWaggons();

	public RailwayStationBusinessConfig withTracks(String... trackNumbers) {
		for (String track : trackNumbers) {
			tracksAndWaggons.putTrack(track);
		}
		return this;
	}

	public RailwayStationBusinessConfig withWaggons(String trackNumber, String... waggonNumbers) {
		tracksAndWaggons.putWaggons(trackNumber, waggonNumbers);
		return this;
	}
}