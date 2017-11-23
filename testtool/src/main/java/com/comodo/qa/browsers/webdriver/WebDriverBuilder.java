package com.comodo.qa.browsers.webdriver;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.comodo.qa.browsers.reporter.Exception;
import com.comodo.qa.browsers.testrunner.TestRunner;

public class WebDriverBuilder
{
	//Region: Constants
	public static final String MOZILLA_WEBDRIVER = "Firefox";
	public static final String ICEDRAGON_WEBDRIVER = "IceDragon";
	public static final String USER_DIR = "user.dir";
	
	public String binaryPath;
	public String CurrentWebDriver = null; 
	protected boolean BrowserAlreadyLoaded = false;
	public WebDriver driver = null;
	protected WebDriver ExistingMozillaBrowser;
	protected WebDriver ExistingIcedragonBrowser;
	
	protected Process process;
	
    //Region: Load web browser
	private void CheckExistingWebDriverInstance() {
		if(CurrentWebDriver.equalsIgnoreCase(MOZILLA_WEBDRIVER) && ExistingMozillaBrowser != null) {
			driver = ExistingMozillaBrowser;
			return;
		}
		else if(CurrentWebDriver.equalsIgnoreCase(ICEDRAGON_WEBDRIVER) && ExistingIcedragonBrowser != null) {
			driver = ExistingIcedragonBrowser;
			return;
		}
	}
	
	private void ManageLoadWebDriver() {
		if(binaryPath == null || binaryPath.isEmpty()) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("WEBDRIVER"),
					"BinaryPath not provided.");
			return;
		}
		
		if(CurrentWebDriver.equalsIgnoreCase(MOZILLA_WEBDRIVER)) {
			SetFirefoxDriver();
		}
		else if(CurrentWebDriver.equalsIgnoreCase(ICEDRAGON_WEBDRIVER)) {
			SetIceDragonDriver();
		}
	}
	
	private void SetFirefoxDriver() {		 
		driver = new FirefoxDriver(GetFirefoxBinary(), null);
		ExistingMozillaBrowser = driver;
		
		TestRunner.reporter.addExecutionInfo("Firefox Driver Instance loaded successfully.");
	}

	private FirefoxBinary GetFirefoxBinary() {
		String firefoxBinPath = binaryPath;
		File firefoxBinary = new File(firefoxBinPath);
		
		return new FirefoxBinary(firefoxBinary); 
	}
	
	private void SetIceDragonDriver() {		
		FirefoxBinary binary = GetIcedragonBinary();

		String profilePath = "D:\\4abcp8if.default";
		FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
		
		driver = new FirefoxDriver(binary, profile);
		System.out.println("Driver is null: " + (driver == null));
		ExistingIcedragonBrowser = driver;
		
		TestRunner.reporter.addExecutionInfo("IceDragon Driver Instance loaded successfully.");
	}
	
	private FirefoxBinary GetIcedragonBinary() {
		File pathToIcedragonBinary = new File(binaryPath);
		
		FirefoxBinary binary = new FirefoxBinary(pathToIcedragonBinary);
		return binary;  
	}
	
	private void ManageDriverSettings() {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public void loadWebBrowser() {
		if(TestRunner.reporter == null || !TestRunner.reporter.isClean()) return;
		
		CheckExistingWebDriverInstance();
		if(driver != null) return;
		ManageLoadWebDriver();
		ManageDriverSettings();			
	}
	
	//Region: Close web browser
	public void closeWebBrowser() {
		if(driver == null) return;
		driver.quit();
		ExistingMozillaBrowser = null;
		ExistingIcedragonBrowser = null;
	}
	
}
