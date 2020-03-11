package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.unittest.departtrain.businesslogic.enumeration.DepartmentOrderState;
import org.camunda.bpm.unittest.departtrain.util.RailTestUtil;

import lombok.Data;

@Data
public class DepartmentOrder {
	
	private DepartmentOrderState departmentOrderState = DepartmentOrderState.ACTIVE;

	private Track targetTrack;
	
	private List<Waggon> waggons;

	public boolean containsAnyWaggon(List<String> waggonNumbers) {
		HashMap<String, Waggon> hashedWaggons = RailTestUtil.hashWaggons(waggons);
		for (String waggonNumber : waggonNumbers) {
			if (hashedWaggons.get(waggonNumber) != null) {
				return true;
			}
		}
		return false;
	}
}