package org.camunda.bpm.unittest.departtrain.businesslogic;

public class RailwayStationBusinessLogic implements IRailwayStationBusinessLogic {
	
	private static RailwayStationBusinessLogic instance;
	
	private RailwayStationBusinessLogic() {
		// ...
	}

	@Override
	public boolean waggonsReadyToGo(TrainConfig trainConfig) {
		return false;
	}

	@Override
	public boolean waggonsAvaiableForShuntingOrder() {
		return true;
	}
	
	public void init(RailwayStationBusinessConfig railwayStationBusinessConfig) {
		// TODO Auto-generated method stub
	}
	
	public void print() {
		// TODO Auto-generated method stub
	}
	
	// ---

	public static RailwayStationBusinessLogic getInstance() {
		if (instance == null) {
			instance = new RailwayStationBusinessLogic();
		}
		return instance;
	}
}