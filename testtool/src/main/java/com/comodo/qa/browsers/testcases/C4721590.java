package com.comodo.qa.browsers.testcases;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comodo.qa.browsers.reusable.IcedragonHomepage;
import com.comodo.qa.browsers.testrunner.TestRunner;

/*
 * Test if Yahoo Homepage default URL is correct for United States (English)
 */
public class C4721590 {
	@Test
	public void testC4721590(){
		IcedragonHomepage.TestHomepage("US", "C4721590");
		assertTrue(TestRunner.reporter.isClean());
	}
	
}
