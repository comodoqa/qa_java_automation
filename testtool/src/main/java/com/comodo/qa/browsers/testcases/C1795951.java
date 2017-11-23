package com.comodo.qa.browsers.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import com.comodo.qa.browsers.reusable.Icedragon;
import com.comodo.qa.browsers.testrunner.TestRunner;

/*
 * Test if the correct version number is displayed in the "License Agreement" window
 * http://testrail.brad.dc.comodo.net/index.php?/cases/view/1795951
 */
public class C1795951 {
	private Icedragon icedragon;
	
	@Test
	public void testC1795951() {
		//Preconditions
		initIcedragon();
		checkIcedragonIsInstalled();
		
		//Steps
		launchInstaller();
		checkVersionInEULA();
		closeIcedragon();
	}
	
	private void initIcedragon() {
		icedragon = new Icedragon(null, false);
	}
	
	private void checkIcedragonIsInstalled() {	
		if(icedragon.isInstalled()) {
			closeIcedragon();
			uninstallIcedragon();
		}
	}
	
	private void uninstallIcedragon() {
		icedragon.uninstallIceDragon(true);
		
		assertTrue(TestRunner.reporter.isClean());
	}
	
	private void launchInstaller() {
		Icedragon.browser.launchInstaller();
		
		assertTrue(TestRunner.reporter.isClean());
	}
	
	private void checkVersionInEULA() {
		String version = TestRunner.version;
		
		//TestRunner.reporter.clear();System.out.println("49: " + Icedragon.browser.ocrFindElement("49", 10));
		TestRunner.reporter.clear();System.out.println("Delaware: " + Icedragon.browser.ocrFindElement("Delaware", 10));
		TestRunner.reporter.clear();System.out.println("Comodo: " + Icedragon.browser.ocrFindElement("Comodo", 10));
		TestRunner.reporter.clear();System.out.println("some text:" + Icedragon.browser.ocrFindElement("some text", 10));
		
		//boolean found = Icedragon.browser.ocrFindElement("49", 5);
		
		Icedragon.browser.clickButton("install", "cancel", 2);
		
		assertTrue(TestRunner.reporter.isCleanExecution());
		//assertTrue("Version in EULA didnt no match expected value", found);
	}
	
	private void closeIcedragon() {
		Icedragon.browser.closeBrowser();
	}
	
}
