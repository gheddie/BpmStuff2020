package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.DepartmentOrder;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.DepartmentOrderState;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Track;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailWayException;
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
	public void createDepartureOrder(List<String> waggonNumbers, String businessKey) throws RailWayException {
		
		// no active order --> OK
		List<DepartmentOrder> activeDepartureOrders = findActiveDepartureOrders();
		if ((activeDepartureOrders != null) && (activeDepartureOrders.size() > 0)) {
			throw new RailWayException();
		}
		
		// all waggons must be present in station
		HashMap<String, Waggon> allWaggons = tracksAndWaggons.getAllWaggons();
		for (String waggonNumber : waggonNumbers) {
			if (allWaggons.get(waggonNumber) == null) {
				throw new RailWayException();
			}
		}
		
		// none of the waggons must be planned in active departure order!!
		for (DepartmentOrder activeDepartureOrder : activeDepartureOrders) {
			if (activeDepartureOrder.containsAnyWaggon(waggonNumbers)) {
				throw new RailWayException();		
			}
		}
		
		// now, create a department order of state 'ACTIVE'...
		departmentOrders.put(businessKey, new DepartmentOrder());
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

	public void print(boolean showWaggonDefects) {
		System.out.println("---------------------------------------------");
		if (departmentOrders != null) {
			System.out.println(departmentOrders.size() + " department orders:");
			for (DepartmentOrder departmentOrder : departmentOrders.values()) {
				System.out.println(departmentOrder + " (" + departmentOrder.getDepartmentOrderState() + ")");
			}
		} else {
			System.out.println("NO department orders.");
		}
		System.out.println("---tracks an waggons:");
		for (Track track : tracksAndWaggons.getTracks()) {
			System.out.println("track[" + track.getTrackNumber() + "] ---> "
					+ BusinessLogicUtil.formatStringList(tracksAndWaggons.getWaggonNumbers(track.getTrackNumber(), showWaggonDefects)));
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
	
	public int countWaggons() {
		return tracksAndWaggons.getAllWaggons().values().size();
	}
	
	@Override
	public void removeWaggons(List<String> waggonNumbers) {
		for (String waggonNumber : waggonNumbers) {
			tracksAndWaggons.removeWaggon(waggonNumber);
		}
	}
	
	@Override
	public void setDefectCode(String waggonNumber, WaggonErrorCode waggonErrorCode) {
		tracksAndWaggons.setDefectCode(waggonNumber, waggonErrorCode);
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