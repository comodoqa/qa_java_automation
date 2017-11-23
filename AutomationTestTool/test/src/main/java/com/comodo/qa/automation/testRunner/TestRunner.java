package com.comodo.qa.automation.testRunner;

import com.comodo.qa.automation.logger.Loggers;
import com.comodo.qa.automation.resources.LoggerStringRepository;
import com.comodo.qa.automation.testSetup.Execution;
import com.comodo.qa.automation.testSetup.TestCase;
import com.comodo.qa.automation.testSetup.TestSetup;

public class TestRunner {
	public static Loggers loggers;
	
	public static void main(String[] args) {
		initLoggers();
		manageTestSetup();
		manageRunExecutions();
			
		if(loggers.getErrorCount() == 0) {
			System.out.println(String.format("%s%s%s",
					System.lineSeparator(),
					LoggerStringRepository.CONSOLE_N0_ERRORS,
					System.lineSeparator()));
		} else {
			System.out.println("");
			System.out.println(String.format(LoggerStringRepository.CONSOLE_ERRORS, 
				loggers.getErrorCount()));
		}		
	}

	private static void initLoggers() {
		loggers = new Loggers();
	}
	
	private static void manageTestSetup() {
		TestSetup setup = new TestSetup();
		setup.RunSetup();
	}
	
	private static void manageRunExecutions() {
		if(TestSetup.executionList == null || TestSetup.executionList.size() == 0) return;
		
		System.out.println("");
		for(Execution execution : TestSetup.executionList) {
			for(TestCase testCase : execution.getTestCases()) {
				TestCaseRunner tcr = new TestCaseRunner(testCase);
				tcr.execute();		
			}
		}
	}
}
