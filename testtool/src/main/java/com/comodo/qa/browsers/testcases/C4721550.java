package com.comodo.qa.browsers.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.comodo.qa.browsers.reusable.IcedragonHomepage;
import com.comodo.qa.browsers.testrunner.TestRunner;

/*
 * Test if Yahoo Homepage default URL is correct for Germany
 */
public class C4721550 {
	@Test
	public void testC4721550() { 
		IcedragonHomepage.TestHomepage("GERMANY", "C4721550");
		assertTrue(TestRunner.reporter.isClean());	
	}
	
}
