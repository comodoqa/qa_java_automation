package com.comodo.qa.browsers.reusable;

import com.comodo.qa.browsers.properties.SystemProperties;
import com.comodo.qa.browsers.reporter.Exception;
import com.comodo.qa.browsers.testrunner.TestRunner;

public class IcedragonHomepage {
	public static Icedragon icedragon;
	
	public static boolean TestHomepage(String nation, String caseId) {		
		//Preconditions
		InitIcedragon();
		CheckIcedragonIsInstalled();

		//Steps
		CloseIcedragon();
		DeleteAppData();
		ChangeRegion(nation, caseId);
		LaunchIcedragon();
		SkipImport();
		ClickHomePageButton();
		MatchAddress(nation);		
		CloseIcedragon();
		
		if(TestRunner.reporter.isClean()) return true;
		return false;
	}
	
	private static void InitIcedragon() {
		if(!TestRunner.reporter.isClean()) return;
		
		icedragon = new Icedragon(null, false);
	}
	
	private static void CheckIcedragonIsInstalled() {	
		if(!icedragon.isInstalled()) {
			InstallIcedragon();
			ClickNotNow();
			CloseIcedragon();
			WaitFor();
		}
	}
	
	private static void InstallIcedragon() {
		if(!TestRunner.reporter.isClean()) return;
		
		icedragon.installIceDragon();
	}
	
	private static void ClickNotNow() {
		if(!TestRunner.reporter.isClean()) return;
		
		icedragon.clickNotNow();
	}
	
	private static void CloseIcedragon() {
		if(!TestRunner.reporter.isClean()) return;
		
		WaitFor();
		Icedragon.browser.closeBrowser();
	}
	
	private static void WaitFor() {
		if(!TestRunner.reporter.isClean()) return;
		
		Icedragon.browser.waitFor(500);
	}
	
	private static void DeleteAppData() {
		if(!TestRunner.reporter.isClean()) return;
		
		WaitFor();
		
		if(!TestRunner.reporter.isClean()) return;
		Icedragon.DeleteAppData();
	}
	
	private static void ChangeRegion(String nation, String caseId) {
		if(!TestRunner.reporter.isClean()) return;
		
		String result = Common.ChangeRegion(SystemProperties.NATION.get(nation));
		if(!result.contains(SystemProperties.NATION.get(nation))) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("RUNTIME"),
					String.format("Failed to change region to [%s] in [%s]" + "(0)", 
							nation, caseId));
		}
	}
	
	private static void LaunchIcedragon() {
		if(!TestRunner.reporter.isClean()) return;
		
		Icedragon.browser.launchBrowser();	
	}
	
	
	private static void SkipImport() {
		if(!TestRunner.reporter.isClean()) return;
		
		icedragon.skipImport();
	}
	
	private static void ClickHomePageButton() {
		if(!TestRunner.reporter.isClean()) return;
		
		icedragon.clickNotNow();
		if(!TestRunner.reporter.isClean()) return;
		
		icedragon.clickHomePage();
	}
	
	private static void MatchAddress(String nation) {
		if(!TestRunner.reporter.isClean()) return;
		
		boolean isMatch = Icedragon.browser.findElement("test", String.format("%s Home Page", nation), 3);
		
		if(!isMatch) {
			TestRunner.reporter.addExecutionResult(Exception.SOURCES.get("EXECUTION"),
					"Default homepage did no match with expected string");
		}
	}
	
}
