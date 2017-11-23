package com.comodo.qa.browsers.reporter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Reporter {
	private static final String LOG4J_PROPERTIES = "log4j.properties";
	
	private List<ExecutionResult> executionResults;
	private Logger infoLogger;
	private Logger errorLogger;
	private boolean isReportingEnabled;
	
	public Reporter() {
		executionResults = new ArrayList<ExecutionResult>();
		
		initLoggers();
	}
	
	public List<ExecutionResult> getExecutionResults() {
		if(!isValid()) return null;
		
		return this.executionResults;
	}
	
	public void setExecutionResults(List<ExecutionResult> executionResults) {
		if(!isValid()) return;
		
		this.executionResults = executionResults;
	}
	
	public Logger getInfoLogger() {
		if(!isValid()) return null; 
		
		return this.infoLogger;
	}
	
	public void setInfoLogger(Logger infoLogger) {
		if(!isValid()) return;
		
		this.infoLogger = infoLogger;
	}
		
	public Logger getErrorLogger() {
		if(!isValid()) return null;
		
		return this.errorLogger;
	}
	
	public void setErrorLogger(Logger errorLogger) {
		if(!isValid()) return;
		
		this.errorLogger = errorLogger;
	}
	
	public boolean getIsReportingEnabled() {
		if(!isValid()) return false;
		
		return this.isReportingEnabled;
	}
	
	public void setIsReportingEnabled(boolean isReportingEnabled) {
		if(!isValid()) return;
		
		if(isReportingEnabled) {
			infoLogger.info("TestRail reporting turned ON.");
		} else {
			infoLogger.info("TestRail reporting turned OFF.");
		}
		
		this.isReportingEnabled = isReportingEnabled;
	}
	
	private void initLoggers() {
		Reporter.class.getResourceAsStream(LOG4J_PROPERTIES);
		infoLogger = Logger.getLogger("infoLogger");
		infoLogger.info("...");
		infoLogger.info("Info Logger started.");
		errorLogger = Logger.getLogger("errorLogger");
		errorLogger.error("...");
		infoLogger.info("Error Logger started.");
	}
	
	private boolean isValid () {
		if(errorLogger == null) {
			System.out.println("!!!Error logger not initialized");
			return false;
		}
		
		if(infoLogger == null) {
			System.out.println("!!!Info logger not initialized");
			return false;
		}
		
		if(executionResults == null) {
			errorLogger.error("ExecutionResult Handler not Initialized");
			return false;
		}
		
		return true;
	}
	
	public void addExecutionResult(ExecutionResult executionResult) {
		if(!isValid()) return; 
		
		executionResults.add(executionResult);
		errorLogger.error(executionResult.getMessage());
	}
	
	public void addExecutionResult(int sourceId, String message) {
		if(!isValid()) return; 
		
		ExecutionResult executionResult = new ExecutionResult();
		executionResult.setSource(sourceId);
		executionResult.setMessage(message);
		
		executionResults.add(executionResult);
		errorLogger.error(executionResult.getMessage());
	}
	
	public void addExecutionInfo(String message) {
		if(!isValid()) return; 
		
		infoLogger.info(message);
		System.out.println(message);
	}
	
	public String getPrintableMessage(ExecutionResult executionResult) {
		String printableMessage = null;
		
		printableMessage = String.format("%s executionResult: %s", 
				executionResult.getSourceName(), 
				executionResult.getMessage());
		
		return printableMessage;
	}
	
	public List<String> getReportableExecutionResults() {
		List<String> reportableExecutionResults = new ArrayList<String>();
		
		for(ExecutionResult executionResult : this.executionResults) {
			if(executionResult.getSource() == Exception.SOURCES.get("EXECUTION")) {
				reportableExecutionResults.add(executionResult.getMessage());
			}
		}
		
		return reportableExecutionResults;
	}
	
	public List<String> getAllExecutionResults() {
		List<String> reportableExecutionResults = new ArrayList<String>();
		
		for(ExecutionResult executionResult : this.executionResults) {
			reportableExecutionResults.add(executionResult.getMessage());
		}
		
		return reportableExecutionResults;
	}
	
	public boolean isClean() {
		if (this.executionResults.isEmpty()) return true;
		for(ExecutionResult executionResult : this.executionResults) {
			if(executionResult.getSource() != Exception.SOURCES.get("EXECUTION")) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isCleanExecution() {
		if (this.executionResults.isEmpty()) return true;
		
		return false;
	}
	
	public void clear() {
		if(this.executionResults == null || this.executionResults.isEmpty()) return;
		
		this.executionResults.clear();
	}
	
}
