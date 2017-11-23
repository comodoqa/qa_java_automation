package com.comodo.qa.browsers.properties;

import java.util.ArrayList;

public class BrowserDefaultPropertyRepository {	
	public ArrayList<Details> BROWSER_TYPES = new ArrayList<Details>(){
		private static final long serialVersionUID = 1L;
	{
		add(ICE_DRAGON_DEFAULTS);
		add(DRAGON_DEFAULTS);
		add(CHROMODO_DEFAULTS);
	}};
	
	public static Details ICE_DRAGON_DEFAULTS = new Details() {{
		id = 1;
		name = "IceDragon";
		defaultPath =  "\\Comodo\\IceDragon\\";
		appExec =  "icedragon.exe";
		uninstallExec = "uninstall.exe";
		key86 = "SOFTWARE\\ComodoGroup\\IceDragon";
		key64 = "SOFTWARE\\Wow6432Node\\ComodoGroup\\IceDragon";
	}};
	
	public static Details DRAGON_DEFAULTS = new Details(){{
		id = 2;
		name = "Dragon";
		defaultPath =  "\\Comodo\\Dragon\\";
		appExec =  "dragon.exe";
		uninstallExec = "uninstall.exe";
	}};
	
	public static Details CHROMODO_DEFAULTS = new Details(){{
		id = 3;
		name = "Chromodo";
		defaultPath =  "\\Comodo\\Chromodo\\";
		appExec =  "chromodo.exe";
		uninstallExec = "uninstall.exe";
	}};
	
}
