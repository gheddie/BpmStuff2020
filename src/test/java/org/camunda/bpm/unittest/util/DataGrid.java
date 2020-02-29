package org.camunda.bpm.unittest.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

public class DataGrid<T> {

	private static final String SUBVALUE_NULL = "<Hauptwert nicht gesetzt>";

	private HashMap<String, Integer> headersMaxLengths = new HashMap<String, Integer>();

	private int dividerLength;

	private DataGridConfiguration configuration;

	private List<T> entities;

	public void print(List<T> entities) {
		
		this.entities = entities;
		
		if (configuration == null) {
			configuration = DataGridConfiguration.fromValues(null);
		}
		
		if (configuration.getHeaders() == null) {
			configuration.guessHeaders(entities.get(0));
		}

		String header = null;
		int maxIndexLength = -1;
		for (int headerIndex = 0; headerIndex < configuration.getHeaders().size(); headerIndex++) {
			header = configuration.getHeaders().get(headerIndex);
			maxIndexLength = getMaxIndexLength(headerIndex);
			headersMaxLengths.put(header, maxIndexLength);
		}

		String divider = generateDivider();

		System.out.println(divider);
		for (String h : configuration.getHeaders()) {
			System.out.print("|");
			System.out.print(extend(h, headersMaxLengths.get(h)));
		}
		System.out.print("|");
		System.out.println();
		System.out.println(divider);

		for (Object e : entities) {
			System.out.print("|");
			for (String h : configuration.getHeaders()) {
				System.out.print(extend(getFieldValue(e, h), headersMaxLengths.get(h)));
				System.out.print("|");
			}
			System.out.println();
			System.out.println(divider);
		}
	}

	private String generateDivider() {
		for (String h : configuration.getHeaders()) {
			dividerLength += headersMaxLengths.get(h);
		}
		dividerLength += configuration.getHeaders().size() + 1;
		String div = "";
		for (int i = 0; i < dividerLength; i++) {
			div += "-";
		}
		return div;
	}

	private String extend(String fieldValue, Integer extendToLength) {
		int extendBy = extendToLength - fieldValue.length();
		for (int i = 0; i < extendBy; i++) {
			fieldValue += " ";
		}
		return fieldValue;
	}

	private int getMaxIndexLength(int index) {

		int maxLength = Integer.MIN_VALUE;

		if (configuration.getHeaders().get(index).length() > maxLength) {
			maxLength = configuration.getHeaders().get(index).length();
		}
		String value = null;
		for (Object e : entities) {
			value = getFieldValue(e, configuration.getHeaders().get(index));
			if (value.length() > maxLength) {
				maxLength = value.length();
			}
		}
		return maxLength;
	}

	private String getFieldValue(Object o, String header) {
		try {
			Field declaredField = null;
			if (header.contains("@")) {
				String[] spl = header.split("@");
				Field subField = o.getClass().getDeclaredField(spl[0]);
				subField.setAccessible(true);
				Object subValue = subField.get(o);
				if (subValue == null) {
					return SUBVALUE_NULL;
				}
				declaredField = subValue.getClass().getDeclaredField(spl[1]);
				declaredField.setAccessible(true);
				return String.valueOf(declaredField.get(subValue));
			} else {
				declaredField = o.getClass().getDeclaredField(header);
				declaredField.setAccessible(true);
				return String.valueOf(declaredField.get(o));
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataGrid withConfiguration(DataGridConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}

	public void printWithHeaders(List<T> entities, String... aHeaders) {
		withConfiguration(DataGridConfiguration.fromValues(Arrays.asList(aHeaders)));
		print(entities);
	}
}