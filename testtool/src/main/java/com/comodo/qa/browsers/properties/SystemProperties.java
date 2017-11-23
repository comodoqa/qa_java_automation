package com.comodo.qa.browsers.properties;

import java.util.HashMap;
import java.util.Map;

public class SystemProperties {
	public static final String PROGRAMFILES_PATH_32 = "C:\\Program Files";
	public static final String PROGRAMFILES_PATH_64 = "C:\\Program Files (x86)";
	
	public static final Map<String, String> NATION = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
	{
		put("US", "244");
		put("GERMANY", "94");
	}};
	
}
