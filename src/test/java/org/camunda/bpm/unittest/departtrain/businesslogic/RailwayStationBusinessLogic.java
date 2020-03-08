package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.DepartmentOrder;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.DepartmentOrderState;
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
	public boolean waggonsAvailableForDepartureTrain(List<String> waggons, String businessKey) {
		
		// TODO
		List<DepartmentOrder> activeDepartureOrders = findActiveDepartureOrders();
		if ((activeDepartureOrders != null) && (activeDepartureOrders.size() > 0)) {
			return false;
		}
		
		// none of the waggons must be planned in active departure order!!
		for (DepartmentOrder activeDepartureOrder : activeDepartureOrders) {
			if (activeDepartureOrder.containsAnyWaggon(waggons)) {
				return false;		
			}
		}
		
		// now, create a department order of state 'ACTIVE'...
		departmentOrders.put(businessKey, new DepartmentOrder());

		return true;
	}

	private List<DepartmentOrder> findActiveDepartureOrders() {
		List<DepartmentOrder> activeOrders = new ArrayList<DepartmentOrder>();
		for (DepartmentOrder departmentOrder : departmentOrders.values()) {
			if (departmentOrder.getDepartmentOrderState().equals(DepartmentOrderState.ACTIVE)) {
				activeOrders.add(departmentOrder);
			}
		}
		return activeOrders;
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
	
	@Override
	public void cancelDepartureOrder(String businessKey) {
		departmentOrders.get(businessKey).setDepartmentOrderState(DepartmentOrderState.CANCELLED);
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