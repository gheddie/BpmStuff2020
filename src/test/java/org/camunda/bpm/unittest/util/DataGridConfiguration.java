package org.camunda.bpm.unittest.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DataGridConfiguration {

	private List<String> headers;

	private DataGridConfiguration(List<String> headers) {
		super();
		this.headers = headers;
	}

	public static DataGridConfiguration fromValues(List<String> aHeaders) {
		return new DataGridConfiguration(aHeaders);
	}

	public void guessHeaders(Object object) {
		headers = new ArrayList<String>();
		for (Field f : object.getClass().getDeclaredFields()) {
			headers.add(f.getName());
		}
	}
}