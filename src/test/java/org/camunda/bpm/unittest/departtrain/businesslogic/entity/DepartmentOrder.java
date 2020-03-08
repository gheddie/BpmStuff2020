package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class DepartmentOrder {
	
	private DepartmentOrderState departmentOrderState = DepartmentOrderState.ACTIVE;

	private Track targetTrack;
	
	private List<Waggon> waggons;

	public boolean containsAnyWaggon(List<String> waggonNumbers) {
		HashMap<String, Waggon> hashedWaggons = hashWaggons();
		for (String waggonNumber : waggonNumbers) {
			if (hashedWaggons.get(waggonNumber) != null) {
				return true;
			}
		}
		return false;
	}

	private HashMap<String, Waggon> hashWaggons() {
		HashMap<String, Waggon> hashedWaggons = new HashMap<String, Waggon>();
		for (Waggon waggon : waggons) {
			hashedWaggons.put(waggon.getWaggonNumber(), waggon);
		}
		return hashedWaggons; 
	}
}