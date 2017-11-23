package com.comodo.qa.browsers.reporter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;


public class ExecutionResult {	
	private String message;
	private int source;
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getSource() {
		return this.source;
	}
	
	public String getSourceName() {
		return GetKeyByValue(Exception.SOURCES, source);
	}
	
	public void setSource(int source) {
		this.source = source;
	}
	
	private <T, E> String GetKeyByValue(Map<String, Integer> map, int value) {
	    for (Entry<String, Integer> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
}
