package com.comodo.qa.browsers.reusable;

public class Common {
	public static String ChangeRegion(String region) {
		String result = RegistryActions.writeLocale(region);
		
		return result;
	}
	
}
