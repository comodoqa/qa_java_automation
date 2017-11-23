package com.comodo.qa.browsers.properties;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.sikuli.script.App;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

import com.comodo.qa.browsers.reporter.Exception;
import com.comodo.qa.browsers.testrunner.TestRunner;
import com.comodo.qa.browsers.webdriver.WebDriverBuilder;

public class Browser {
	//public static Reporter TestRunner.reporter;
	public BrowserDefaultProperties defaultProperties;
	public String runPath;
	public String uninstallPath;
	protected Integer windowsVersion;
	protected boolean useSelenium;
	
	protected Process process;
	protected Screen screen;	
	protected WebDriver driver = null;
	
	//REGION: PUBLIC
	//REGION: BROWSER ACTIONS
	public void launchBrowser() {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		if(useSelenium){
			WebDriverBuilder driverBuilder = new WebDriverBuilder();
			driverBuilder.binaryPath = runPath;
			driverBuilder.CurrentWebDriver = defaultProperties.name;
			driverBuilder.loadWebBrowser();
			driver = driverBuilder.driver;
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				process = runtime.exec(runPath);
			} catch (IOException e) {		
				TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
						String.format("Failed to open browser: %s.", defaultProperties.name));
			}
		}
	} 

	public void closeBrowser() {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		if(useSelenium){
			if(driver == null) {
				ForceCloseBrowser();
			} else {
				driver.quit();
			}
		} else {
			ForceCloseBrowser();
		}
	}

	private void ForceCloseBrowser() {
		App app = null;
		do {	
			app = App.focus(defaultProperties.name);
			if(app.isRunning()) {
				clickButton("run", "X", 3, true, 3);
				app.close();
			}
		} while(app.isRunning());
	}
	
	public void clickButton(String phase, String button, long timeout, int repeat) {
		clickButton(phase, button, timeout, false, repeat);
	}

	public void clickButton(String phase, String button, long timeout) {
		clickButton(phase, button, timeout, false, 3);
	}
	
	public void clickButton(String phase, String button, long timeout, boolean isErrorIfNotFound) {
		clickButton(phase, button, timeout, isErrorIfNotFound, 3);
	}
	
	public void clickButton(String phase, String button, long timeout, boolean isErrorIfNotFound, int repeat) {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;

		String buttonImgFileName = button.toLowerCase().replaceAll("\\s", "");
		buttonImgFileName = String.format("images/%s_%s_%s.png", defaultProperties.type, phase.toLowerCase(), buttonImgFileName);
		String buttonImgFullPath = getCurrentPath() + "\\images\\" + buttonImgFileName.replace("images/", "");
		File image = new File(buttonImgFullPath);
		if(!image.exists()) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					String.format("Image for the %s button is missing(0)", button));
			return;
		}

		try { 
			screen.wait(buttonImgFileName, timeout);
			for(int i=0; i<repeat; i++) {
				screen.click();
			}
		} catch(FindFailed e) {
			if(isErrorIfNotFound) {
				TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("EXECUTION"),
						String.format("Failed to find: \"%s\"", buttonImgFileName));
			}
		}
	}

	public boolean ocrFindElement(String text, long timeout) {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return false;

		try { 
			screen.findText(text, timeout);
			return true;
		} catch(FindFailed e) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("EXECUTION"),
					String.format("(Ocr)Failed to find: \"%s\"", text));
			return false;
		}
	}
	
	public boolean findElement(String phase, String element, long timeout) {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return false;

		String elementImgFileName = element.toLowerCase().replaceAll("\\s", "");
		elementImgFileName = String.format("images/%s_%s_%s.png", defaultProperties.type, phase.toLowerCase(), elementImgFileName);

		String elementImgFullPath = getCurrentPath() + "\\images\\" + elementImgFileName.replace("images/", "");
		File image = new File(elementImgFullPath);
		if(!image.exists()) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					String.format("Image for element %s is missing", element));
			return false;
		}
		
		try { 
			screen.wait(elementImgFileName, timeout);
			return true;
		} catch(FindFailed e) {
			return false;
		}
	}

	public void waitFor(long milis) {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					"Action interrupted.");
		}
	}

	//REGION: UNINSTALLER
	public void launchUninstaller() {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;

		Runtime runtime = Runtime.getRuntime();
		try {
			process = runtime.exec(uninstallPath);
		} catch (IOException e) {	
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					String.format("Failed to launch browser uninstaller for: %s.", defaultProperties.name));
		}
	}

	//REGION: INSTALLER
	public void launchInstaller() {
		String installerPath = getInstallerPath();
		if(installerPath == null) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					String.format("Failed to launch browser setup for: %s.", defaultProperties.name));
			return;
		}
		LaunchInstaller(installerPath);
	}

	private String getInstallerPath() {
		File folder = new File(getCurrentPath());
		for(File fileEntry : folder.listFiles()){
			if(fileEntry.getName().contains(defaultProperties.name.toLowerCase())
					|| fileEntry.getName().contains("setup")
					|| fileEntry.getName().contains(".exe")){

				return fileEntry.getPath();
			}
		}

		return null;
	}

	private String getCurrentPath() {
		File jar = new File(Browser
				.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath());
		String path = jar.getParentFile().getPath();

		return path.replace("%20", " ");
	}

	private void LaunchInstaller(String installerPath) {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;

		Runtime runtime = Runtime.getRuntime();
		try {
			process = runtime.exec(installerPath);
		} catch (IOException e) {	
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("BROWSER"),
					String.format("Failed to launch browser installer for %s", defaultProperties.name));
		}
	}

}
