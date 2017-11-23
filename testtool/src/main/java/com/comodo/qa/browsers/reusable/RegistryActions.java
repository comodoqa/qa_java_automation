package com.comodo.qa.browsers.reusable;

import com.comodo.qa.browsers.tools.WindowsRegistry;

public class RegistryActions {
	public static String readNation() {
		int hkey = WindowsRegistry.HKEY_CURRENT_USER;
		String key = "Control Panel\\International\\Geo";
		String valueName = "Nation";

		String value = null;
		try{
			value = WindowsRegistry.readString(hkey, key, valueName);
		} catch(Exception e) {
			return null;
		}
		
		return value;
	}
	
	public static String writeLocale(String value) {
		int hkey = WindowsRegistry.HKEY_CURRENT_USER;
		String key = "Control Panel\\International\\Geo";
		String valueName = "Nation";
		
		try {
			WindowsRegistry.writeStringValue(hkey, key, valueName, value);
		} catch(Exception e) {
			return null;
		}
		
		return readNation();
	}
	
}
