package org.camunda.bpm.unittest.util;

import java.util.HashMap;

public class HashBuilder {
	
	private HashMap<String, Object> values = new HashMap<String, Object>();

	private HashBuilder() {
		// ...
	}

	public static HashBuilder create() {
		return new HashBuilder();
	}

	public HashBuilder withValue(String key, Object value) {
		values.put(key, value);
		return this;
	}

	public HashMap<String, Object> build() {
		return values;
	}
}