package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import java.util.List;

import lombok.Data;

@Data
public class DepartmentOrder {
	
	private DepartmentOrderState departmentOrderState;

	private Track targetTrack;
	
	private List<Waggon> waggons;
}