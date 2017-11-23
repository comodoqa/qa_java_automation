package com.comodo.qa.automation.testRunner;

import java.util.Collections;

import com.comodo.qa.automation.actions.Action;
import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testSetup.TestCase;
import com.comodo.qa.automation.testSetup.TestSetup;
import com.comodo.qa.automation.testrail.TestRailBuilder;
import com.comodo.qa.browsers.tools.WindowsVersion;

public class TestCaseRunner {
	private TestCase testCase = null;
	private Boolean _continue = null;
	private long startTime = 0;
	private long endTime = 0;
	private long elapsed = 0;
	public static String assertFailMessage = null;
	public static String validationMessage = null;
	public static String reportingErrorMessage = null;
	
	public TestCaseRunner(TestCase testCase) {
		assertFailMessage = null;
		validationMessage = null;
		reportingErrorMessage = null;
		this.testCase = testCase;
		_continue = true;
	}
	
	public void execute(){
		resetTimers();
		showStartMessage();
		setStartTime();
		
		runTestCase();
		showEndMesssage();
		
		rerunTestCase();
		
		setEndTime();
		reportToTestRail();
	}
	
	private void resetTimers() {
		startTime = 0;
		endTime = 0;
		elapsed = 0;
	}
	
	private void setStartTime() {
		startTime = System.currentTimeMillis();
	}
	
	private void setEndTime() {
		endTime = System.currentTimeMillis();
		elapsed = endTime - startTime;
	}
	
	private void rerunTestCase() {
		if(TestSetup.settings.getRerunIfFailed() > 0 && assertFailMessage != null) {
			int repeated = 0;
			while(repeated < TestSetup.settings.getRerunIfFailed() && assertFailMessage != null) {
				_continue = true;
				assertFailMessage = null;
				showStartRerunMessage();
				runTestCase();
				showEndRerunMesssage();
				repeated ++;
			}
		}
	}
	
	private void runTestCase() {
		Collections.sort(testCase.getActionList());	
		for(Action action : testCase.getActionList()) {
			if(!action.isValidAction()) {
				showStoppedMessage();
				break;
			}
			ActionRunner ar = new ActionRunner(action);
			_continue = ar.execute();
			if(!_continue) {
				showStoppedMessage();
				break;
			}
		}
	}
	
	private void reportToTestRail() {
		if(!TestSetup.settings.isTestRailReportingEnabled()) return;
		if(validationMessage != null) return;
		
		TestRailBuilder rb = new TestRailBuilder();
		String message = (assertFailMessage == null) ? "" : ("Fail reason: " + assertFailMessage);
		rb.setData(assertFailMessage == null , "[Executed by test automation tool on computer: " + WindowsVersion.getHostname() + "] " + message, elapsed);
		rb.postResult(testCase.getExecution().getRunId(), testCase.getCaseId());
		reportingErrorMessage = rb.resultMessage;
			
		showReportingErrorMessage();
	}
	
	private void showStartMessage() {
		System.out.println("Executing : " + testCase.getId() + " @ " + testCase.getCaseId());
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.INFOLOGS_START_EXECUTING_TEST_CASE, 
						testCase.getId(),
						testCase.getCaseId()));
	}
	
	private void showStartRerunMessage() {
		System.out.println("Reruning : " + testCase.getId() + " @ " + testCase.getCaseId());
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.INFOLOGS_START_RERUNING_TEST_CASE, 
						testCase.getId(),
						testCase.getCaseId()));
	}
	
	private void showReportingErrorMessage(){
		if(reportingErrorMessage == null || reportingErrorMessage.isEmpty()) return;
		
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.INFOLOGS_FAILED_TO_REPORT_TO_TESTRAIL, 
						reportingErrorMessage));
	}
	
	
	private void showStoppedMessage() {
		TestRunner.loggers.appendInfo( 
						String.format(TestSetupStringRepository.INFOLOGS_STOPPED_EXECUTING_TEST_CASE, 
								testCase.getId(),
								validationMessage != null ? validationMessage : assertFailMessage));
	}
	
	private void showEndMesssage() {
		System.out.println("Finished executing : " 
					+ testCase.getId() 
					+ " @ " 
					+ testCase.getCaseId() 
					+ " with the status: " 
					+ ((assertFailMessage == null && validationMessage == null) ? "passed" : "failed"));
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.INFOLOGS_END_EXECUTING_TEST_CASE, 
						testCase.getId(),
						(assertFailMessage == null && validationMessage == null) ? "passed" : "failed"));
	}
	
	private void showEndRerunMesssage() {
		System.out.println("Finished reruning : " 
					+ testCase.getId() 
					+ " @ " 
					+ testCase.getCaseId() 
					+ " with the status: " 
					+ ((assertFailMessage == null && validationMessage == null) ? "passed" : "failed"));
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.INFOLOGS_END_RERUNING_TEST_CASE, 
						testCase.getId(),
						(assertFailMessage == null && validationMessage == null) ? "passed" : "failed"));
	}
	
}
