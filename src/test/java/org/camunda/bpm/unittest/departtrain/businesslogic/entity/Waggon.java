package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Waggon implements Serializable {

	private static final long serialVersionUID = -8475390197943554789L;
	
	private String waggonNumber;
	
	private WaggonErrorCode waggonErrorCode;
}