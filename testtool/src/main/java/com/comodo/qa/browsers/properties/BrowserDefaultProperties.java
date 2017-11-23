package com.comodo.qa.browsers.properties;

import com.comodo.qa.browsers.tools.WindowsVersion;

public class BrowserDefaultProperties {	
	private static BrowserDefaultPropertyRepository defaultPropertyRepository = new BrowserDefaultPropertyRepository();
	
	public String name;
	public int type;
	public String defaultPath;
	public String appExec;
	public String uninstallExec;
	
	public String defaultRunPath;
	public String defaultUninstallPath;
	public String registryKey;

	public BrowserDefaultProperties(int type) {
		this.type = type;
		for(Details details : defaultPropertyRepository.BROWSER_TYPES) {
			if(details.id == type) {
				this.name = details.name;
				this.appExec = details.appExec;
				this.uninstallExec = details.uninstallExec;	
				this.defaultPath = details.defaultPath;
				this.registryKey = WindowsVersion.Is64Bit() ? details.key64 : details.key86;
			}
		}
		
		SetDefaultRunPath();
		SetDefaultUninstallPath();
	}
	
	public static boolean isValidBrowserType(int type) {
		for(Details details : defaultPropertyRepository.BROWSER_TYPES) {
			if(details.id == type) {
				return true;
			}
		}
			
		return false;
	}
	
	private void SetDefaultRunPath() {
		String pfPath = GetDefaultProgramFilesPath();
		this.defaultRunPath =  pfPath + this.defaultPath + this.appExec;
	}
	
	private void SetDefaultUninstallPath() {
		String pfPath = GetDefaultProgramFilesPath();
		this.defaultUninstallPath =  pfPath + this.defaultPath + this.uninstallExec;
	}
	
	private String GetDefaultProgramFilesPath() {
		if(WindowsVersion.Is64Bit()){
			return SystemProperties.PROGRAMFILES_PATH_64;
		}
		return SystemProperties.PROGRAMFILES_PATH_32;		
	}
	
}
