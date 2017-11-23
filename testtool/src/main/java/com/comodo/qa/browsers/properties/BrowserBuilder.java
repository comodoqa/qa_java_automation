package com.comodo.qa.browsers.properties;

import org.sikuli.basics.Settings;
import org.sikuli.script.Screen;
import org.sikuli.script.TextRecognizer;

import com.comodo.qa.browsers.reporter.Exception;
import com.comodo.qa.browsers.testrunner.TestRunner;
import com.comodo.qa.browsers.tools.WindowsVersion;

public class BrowserBuilder {
	public Browser browser;
	
	private int browserType;
	
	public BrowserBuilder(int browserType, boolean useSelenium) {
		browser = new Browser();
		
		browser.windowsVersion = WindowsVersion.getWindowsVersion();
		browser.useSelenium = useSelenium;
		this.browserType = browserType;
	}
	
	public void validateCore() {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		ValidateWindowsVersion();
		ValidateBrowserType();
	}
	
	private void ValidateWindowsVersion() {
		if(browser.windowsVersion != null) return;
		
		TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"), 
				"Windows version could not be determined.");
	}
	
	private void ValidateBrowserType() {
		if(BrowserDefaultProperties.isValidBrowserType(this.browserType)) return;
		
		TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"), 
				"Invalid browser type.");
	}
	
	public void setBrowserProperties(String customPath) {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		browser.defaultProperties = new BrowserDefaultProperties(this.browserType);
		if(customPath != null && !customPath.isEmpty()) {
			browser.runPath = customPath + browser.defaultProperties.appExec;
			browser.uninstallPath = customPath + browser.defaultProperties.uninstallExec;
		} else {
			browser.runPath = browser.defaultProperties.defaultRunPath;
			browser.uninstallPath = browser.defaultProperties.defaultUninstallPath;
		}
		
		InitSikuli();
	}
	
	private void InitSikuli() {
		Settings.ActionLogs = false;
		Settings.InfoLogs = false;
		Settings.MoveMouseDelay = 0;
		Settings.OcrTextSearch = true;
		Settings.OcrTextRead = true;
		Settings.OcrLanguage = "eng";
		TextRecognizer.reset();
		
		//OCR.setParameter(param, value);
		
		if(browser.screen == null) browser.screen = new Screen();
	}	
	
}
