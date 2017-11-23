package com.comodo.qa.browsers.tools;
 

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WindowsVersion {
	private static final List<String> osNameTemplates = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
		{
	    	add("win%s");
	    	add("win %s");
	    	add("win  %s");
	    	add("win_%s");
	    	add("win__%s");
	    	add("windows%s");
	    	add("windows %s");
	    	add("windows  %s");
	    	add("windows_%s");
	    	add("windows__%s");
	    	add("w%s");
	    	add("w %s");
	    	add("w  %s");
	    	add("w_%s");
	    	add("w__%s");
	    }
	};	
	private static final List<Integer> osVersions = new ArrayList<Integer>(){
		private static final long serialVersionUID = 1L;
		{
	    	add(7);
	    	add(8);
	    	add(10);
	    }
	};
	
	public static Boolean isWindowsVersion(int version) {
		String osInfo = getOsVersion();
		if(osInfo == null) return null;
		for(String osNameTemplate : osNameTemplates) {
			String osName = String.format(osNameTemplate, version);
			if(osInfo.toLowerCase().contains(osName)){
				return true;
			}
		}
		
		return false;
	}
	
	public static Integer getWindowsVersion() {
		String osInfo = getOsVersion();
		if(osInfo == null) return null;
		for(int version : osVersions) {
			for(String osNameTemplate : osNameTemplates) {
				String osName = String.format(osNameTemplate, version);
				if(osInfo.toLowerCase().contains(osName)){
					return version;
				}
			}
		}
		
		return 0;
	}
	
	public static int getWindowsVersion(String src) {
		for(int version : osVersions) {
			for(String osNameTemplate : osNameTemplates) {
				String osName = String.format(osNameTemplate, version);
				if(src.toLowerCase().contains(osName)){
					return version;
				}
			}
		}
		
		return 0;
	}
	
	private static String getOsVersion() {
		Runtime rt; 
		Process pr; 
		BufferedReader in;
		String line = "";
		String fullOSName = "";
		String SEARCH_TERM = "OS Name:";

		try {
			rt = Runtime.getRuntime();
			pr = rt.exec("SYSTEMINFO");
			in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			while((line=in.readLine()) != null) {
				if(line.contains(SEARCH_TERM)) {
					fullOSName = line.substring(line.lastIndexOf(SEARCH_TERM) 
							+ SEARCH_TERM.length(), line.length()-1);
					break;
				} 
			}

		} catch(IOException ioe) {   
			return null;
		}

		return fullOSName;
	}
      
	public static boolean Is64Bit(){
		boolean is64bit = false;
		if (System.getProperty("os.name").contains("Windows")) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}

		return is64bit;		
	}
	
	public static boolean Is64Bit(String src) {
		if(src.toLowerCase().contains("x64")) return true;
		return false;
	}
	
}