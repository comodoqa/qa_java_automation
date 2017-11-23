package com.comodo.qa.browsers.reporter;

import java.util.HashMap;
import java.util.Map;

public class Exception {
	public static final Map<String, Integer> SOURCES = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
	{
		put("VALIDATION", 1);
		put("EXECUTION", 2);
		put("IO", 3);
		put("BROWSER", 4);
		put("WEBDRIVER", 5);
		put("RUNTIME", 6);
	}};
	
}