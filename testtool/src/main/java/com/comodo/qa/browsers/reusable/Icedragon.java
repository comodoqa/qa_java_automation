package com.comodo.qa.browsers.reusable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FileUtils;
import org.sikuli.script.App;

import com.comodo.qa.browsers.properties.Browser;
import com.comodo.qa.browsers.properties.BrowserBuilder;
import com.comodo.qa.browsers.reporter.Exception;
import com.comodo.qa.browsers.testrunner.TestRunner;
import com.comodo.qa.browsers.tools.WindowsRegistry;

public class Icedragon {	
	public static Browser browser;
	
	public Icedragon(String customPath, boolean useSelenium) {
		BrowserBuilder browserBuilder = new BrowserBuilder(1, useSelenium);
		browserBuilder.validateCore();
		browserBuilder.setBrowserProperties(customPath);
		browser = browserBuilder.browser;
	}
	
	public boolean isInstalled() {
		if(!TestRunner.reporter.isClean()) return false;
		
		File launcher = new File(browser.runPath);
		String versionInRegistry = getRegistryKey("Version");
		
		if(!launcher.exists() && versionInRegistry != null && !versionInRegistry.isEmpty()) {
			uninstallIceDragon(true);
			return false;
		}
		
		return (versionInRegistry != null && 
				!versionInRegistry.isEmpty() &&
				launcher.exists());
	}
	
	private String getRegistryKey(String value) {
		try {
			return WindowsRegistry.readString(
				WindowsRegistry.HKEY_LOCAL_MACHINE, 
				browser.defaultProperties.registryKey, 
				value);
		} catch (IllegalArgumentException e) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					"Illegal Argument on Registry Action");
		} catch (IllegalAccessException e) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					"Illegal Access on Registry Action");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					"Invocation Error on Registry Action");
		}
		
		return null;
	}
	
	public void installIceDragon() {
		if(!TestRunner.reporter.isClean()) return;
		
		browser.launchInstaller();
		browser.clickButton("install", "I Agree", 5);
		browser.clickButton("install", "Install", 3);
		browser.clickButton("install", "Finish", 80);
		browser.clickButton("run", "Toolbar icon", 2, true);
		browser.clickButton("install", "Dont Import", 3, 1);
		browser.clickButton("install", "Next", 3);
	}
	
	public void uninstallIceDragon(boolean isCheckedRemoveUseProfile) {
		if(!TestRunner.reporter.isClean()) return;
		
		browser.launchUninstaller();
		if(isCheckedRemoveUseProfile) {
			browser.clickButton("uninstall", "Remove User Profile", 5, 1);
		}
		browser.clickButton("uninstall", "Uninstall", 2);
		browser.clickButton("uninstall", "Finish", 80);
	}
	
	public void skipImport() {
		if(!TestRunner.reporter.isClean()) return;
		
		App app = App.focus("IceDragon");
		app.waitForWindow();
		
		browser.clickButton("install", "Dont Import", 1, false);
		browser.clickButton("install", "Next", 1, false);
	}
	
	public void clickNotNow() {
		if(!TestRunner.reporter.isClean()) return;
		
		App app = App.focus("IceDragon");
		app.waitForWindow();
		
		browser.clickButton("run", "Not Now", 1, false);
	}
	
	public void maximize() {		
		if(!TestRunner.reporter.isClean()) return;
		
		App app = App.focus("IceDragon");
		app.waitForWindow();
		
		browser.clickButton("run", "Maximize", 1, true);
	}
	
	public void clickHomePage() {
		if(!TestRunner.reporter.isClean()) return;
		
		App app = App.focus("IceDragon");
		app.waitForWindow();
		
		browser.clickButton("run", "Home Page", 2);
	}
	
	//region: static methods
	public static void DeleteAppData() {
		if(!TestRunner.reporter.isClean()) return;
		
		String userHome = System.getProperty("user.home");

		String[] paths = {
				String.format("%s\\AppData\\Roaming\\Comodo\\IceDragon", userHome),
				String.format("%s\\AppData\\Local\\Comodo\\IceDragon", userHome)};
		for(String path : paths) {
			File file = new File(path);
			try {  
				FileUtils.deleteDirectory(file);  
			} catch (IOException e) {  
				TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("RUNTIME"),
						"Failed to delete app data");
			}  
		}
	} 
	
}
