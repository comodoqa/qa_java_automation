package com.comodo.qa.automation.testrail;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testRunner.TestRunner;
import com.comodo.qa.automation.testSetup.TestSetup;

public class TestRailBuilder {
	public static APIClient client = null;
	public String resultMessage = null; 	
	@SuppressWarnings("rawtypes")
	public static Map<String, Comparable> data = null;
		
	@SuppressWarnings("rawtypes")
	public TestRailBuilder() {
		resultMessage = null;
		data = new HashMap<String, Comparable>();
		
		testRailConnection();
	}
	
	private void testRailConnection() {
		if(client != null) return;
		if(TestSetup.settings == null) return;
		
		client = new APIClient(TestSetup.settings.getTestRaillUrl());
		client.setUser(TestSetup.settings.getTestRailUsername());
		client.setPassword(TestSetup.settings.getTestRailPassword());
	}
	
	@SuppressWarnings("rawtypes")
	public void setData(boolean status, String message, long elapsed) {
		if(data == null) data = new HashMap<String, Comparable>();
		
		int isPassed;
		
		if(status) isPassed = 1;
		else isPassed = 5;
		
		data.put("status_id", new Integer(isPassed));
		data.put("comment", message);
		data.put("version", TestSetup.settings.getVersion());
		data.put("elapsed", String.format("%ss", (elapsed % 1000)));
	}
	
	public void postResult(String runId, String testCaseId) {
		if(client == null) testRailConnection();
		
		String _runId = cleanArgument(runId);
		String _testCaseId = cleanArgument(testCaseId);
		
		try {
			JSONObject r = (JSONObject) client.sendPost("add_result_for_case/" + _runId + "/" + _testCaseId, data);
			r.clear();
		} catch (Exception e) {
			resultMessage = "Error posting result to testrail: " + e.getMessage();
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.INFOLOGS_FAILED_TO_REPORT_TO_TESTRAIL, 
							resultMessage));
			return;
		}
		
		data.clear();
	}
	
	private String cleanArgument(String argument) {
		String cleanedArgument = argument.replace("C", "");
		cleanedArgument = cleanedArgument.replace("T", "");
		cleanedArgument = cleanedArgument.replace("R", "");
		
		return cleanedArgument;
	}
	
	public JSONArray getTestCases(String runId) {
		if(client == null) testRailConnection();
		
		String _runId = cleanArgument(runId);
		JSONArray r = null;
		
		try {
			r = (JSONArray) client.sendGet("get_tests/" + _runId);
		} catch (Exception e) {
			resultMessage = "Error importing test cases from testrail: " + e.getMessage();
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.INFOLOGS_FAILED_TO_IMPORT_FROM_TESTRAIL, 
							resultMessage));
		}
		
		return r;
	}

	public JSONObject getRun(String runId) {
		if(client == null) testRailConnection();
		
		String _runId = cleanArgument(runId);
		JSONObject r = null;
		
		try {
			r = (JSONObject) client.sendGet("get_run/" + _runId);
		} catch (Exception e) {
			resultMessage = "Error importing run from testrail: " + e.getMessage();
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.INFOLOGS_FAILED_TO_IMPORT_FROM_TESTRAIL, 
							resultMessage));
		}
		
		return r;
	}
	
}
