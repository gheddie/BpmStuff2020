package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.DepartmentOrder;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Track;
import org.camunda.bpm.unittest.departtrain.businesslogic.util.BusinessLogicUtil;

public class RailwayStationBusinessLogic implements IRailwayStationBusinessLogic {

	private static RailwayStationBusinessLogic instance;

	private TracksAndWaggons tracksAndWaggons = new TracksAndWaggons();

	// key --> business key (also from referring business process)
	private HashMap<String, DepartmentOrder> departmentOrders = new HashMap<String, DepartmentOrder>();

	private static final Random random = new Random();

	private RailwayStationBusinessLogic() {
		// ...
	}

	@Override
	public boolean waggonsReadyToGo(TrainConfig trainConfig) {
		return false;
	}
	
	@Override
	public boolean waggonsAvailableForShuntingOrder(String businessKey, List<String> waggons) {
		
		// TODO
		if ((departmentOrders != null) && (departmentOrders.size() > 0)) {
			return false;
		}
		
		// all waggons must be existent!!
		for (String string : waggons) {

		}

		// none of the waggons must be planned in active departure order!!
		/*
		if (departmentOrders == null || departmentOrders.size() == 0) {
			return true;
		}
		*/
		
		// now, create a department order of state 'ACTIVE'...
		departmentOrders.put(businessKey, new DepartmentOrder());

		return true;
	}

	public void print() {
		System.out.println("---------------------------------------------");
		if (departmentOrders != null) {
			System.out.println(departmentOrders.size() + " department orders.");
		} else {
			System.out.println("NO department orders.");
		}
		System.out.println("---tracks an waggons:");
		for (Track track : tracksAndWaggons.getTracks()) {
			System.out.println("track[" + track.getTrackNumber() + "] ---> "
					+ BusinessLogicUtil.formatStringList(tracksAndWaggons.getWaggonNumbers(track.getTrackNumber())));
		}
		System.out.println("---------------------------------------------");
	}

	public String generateBusinessKey() {
		return String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(random.nextInt(1000));
	}

	// ---

	public static RailwayStationBusinessLogic getInstance() {
		if (instance == null) {
			instance = new RailwayStationBusinessLogic();
		}
		return instance;
	}

	public RailwayStationBusinessLogic withTracks(String... trackNumbers) {
		for (String track : trackNumbers) {
			tracksAndWaggons.putTrack(track);
		}
		return this;
	}
	
	public RailwayStationBusinessLogic withWaggons(String trackNumber, String... waggonNumbers) {
		tracksAndWaggons.putWaggons(trackNumber, waggonNumbers);
		return this;
	}
}