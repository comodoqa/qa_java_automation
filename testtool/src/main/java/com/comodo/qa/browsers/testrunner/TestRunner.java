package com.comodo.qa.browsers.testrunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.comodo.qa.browsers.reporter.Reporter;
import com.comodo.qa.browsers.reporter.Exception;
import com.comodo.qa.browsers.testsetup.Execution;
import com.comodo.qa.browsers.testsetup.ExecutionFactory;
import com.comodo.qa.browsers.testsetup.TestCase;

public class TestRunner 
{
	public static Reporter reporter = null;
	
	static List<String> fileList = null;
	static Execution execution = null;
	static List<Execution> executionList = null;
	static boolean isReportEnabled = true;
	static Result result = null;
	static int caseCount = 0;
	static int successCount = 0;
	static int failCount = 0;
	public static String version = null;

	public static void main( String[] args ) {
		initReporter();
		initArguments(args);
		if (!isValidVersion()) return;
		if (!GetAllCSVFiles()) return;
		InitExecutionList();
		SetExecutions();
		runExecutions();	
	}

	private static void initReporter() {
		reporter = new Reporter();
	}

	private static void initArguments(String[] args) {	
		if(args.length == 0) return;	
		if(reporter == null) return;

		for(String argument : args) {
			if(argument.contains("-no-reporting")) {
				reporter.setIsReportingEnabled(false);
			} else {
				reporter.setIsReportingEnabled(true);
			}
			Pattern caseIdPattern = Pattern.compile("\\d+(?:\\.\\d+)+");
			if(caseIdPattern.matcher(argument).matches()) {
				version = argument;
			} 
		}		
	}
	
	private static boolean isValidVersion() {
		if(reporter == null) return false;
		
		if(version == null) {
			reporter.addExecutionResult(Exception.SOURCES.get("VALIDATION"), "Missing version parameter.");
			return false;
		}
		
		return true;
	}

	private static boolean GetAllCSVFiles() {
		if(reporter == null || !reporter.isClean()) return false;
		InitFileList();

		File folder = new File(GetCurrentPath());
		
		if(folder == null || !folder.exists()) {
			reporter.addExecutionResult(Exception.SOURCES.get("IO"), "Invalid container folder path.");
			return false;
		}

		for(File fileEntry : folder.listFiles()){
			if(fileEntry.getName().contains("csv")){
				fileList.add(fileEntry.getPath());
				reporter.addExecutionInfo(String.format("csv file %s found.",
						fileEntry.getName()));
			}
		}
		
		return true;
	}

	private static void InitFileList() {
		fileList = new ArrayList<String>();
	}

	private static String GetCurrentPath() {
		File jar = new File(TestRunner
				.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath());
		String path = jar.getParentFile().getPath();

		return path.replace("%20", " ");
	}

	private static void InitExecutionList() {
		executionList = new ArrayList<Execution>();
	}

	private static void SetExecutions() {
		if(reporter == null || !reporter.isClean()) return;
		if(fileList == null || fileList.isEmpty()) return;

		ExecutionFactory ef = GetExecutionFactory();
		if(ef == null) return;
		
		for(String filePath : fileList){
			try {
				reporter.addExecutionInfo(String.format("Attempting to read setup from: %s.",
						filePath));
				execution = null;
				execution = ef.ImportFromFile(filePath);
			} catch (IOException e) {	
				reporter.addExecutionResult(Exception.SOURCES.get("IO"), 
						"Reading csv from file failed!");
			}

			if(!execution.validationResults.isEmpty()) {
				reporter.addExecutionResult(Exception.SOURCES.get("VALIDATION"), 
						String.format("Importing execution from %s failed, reason(s):%s%s", 
								filePath, 
								System.lineSeparator(), 
								execution.getPrintableValidationResults()));
			} else {
				execution.setVersion(version);
				executionList.add(execution);
				reporter.addExecutionInfo(String.format("Import execution from %s successful%s%24s%s tests to be run, %s to be skipped, on Windows%s %s.",
						filePath, 
						System.lineSeparator(), "", 
						execution.getTestCases().size(), 
						execution.getSkipped(), 
						execution.getWindowsVersion(), 
						execution.getWindowsArchitecture()));
			}
		}
	}

	private static void runExecutions() {
		if(executionList == null || executionList.isEmpty()) return;
		
		if(reporter == null) return;
		reporter.clear();

		for(Execution execution : executionList) { 	
			reporter.addExecutionInfo("EXECUTION(S) STARTED ...");
			reporter.addExecutionInfo(String.format("Running execution: %s on Windows %s %s.", 
					execution.getName(), 
					execution.getWindowsVersion(), 
					execution.getWindowsArchitecture()));

			caseCount = 0;
			successCount = 0;
			failCount = 0;

			List<TestCase> testCases = execution.getTestCases();
			for(TestCase testCase : testCases) { 
				RunTestCase(testCase);
				UpdateStatistics();

				if(result != null) {	
					String message = reporter.getAllExecutionResults().size() > 0 ? reporter.getAllExecutionResults().get(0) : null;
					reporter.addExecutionInfo((result.wasSuccessful() 
							? String.format("%s PASSED.", 
									testCase.getId())
									: String.format("%s FAILED, reason: %s.", 
											testCase.getId(), message)));
				}
				
				ClearReporter();
			}

			reporter.addExecutionInfo(String.format("Finished running execution %s%s%24s%s tests run: %s set to PASSED, %s set to FAILED.", 
					execution.getName(), 
					System.lineSeparator(), "", 
					caseCount, 
					successCount, 
					failCount));
		}
	}

	private static void RunTestCase(TestCase testCase) {
		if(reporter == null || !reporter.isClean()) return;
		
		String classPath = String.format("com.comodo.qa.browsers.testcases.%s", 
				testCase.getCaseId());
		Class<?> c = null;
		result = null;
		try {
			c = Class.forName(classPath);
			result = JUnitCore.runClasses(c);
		} catch (ClassNotFoundException e) {
			reporter.addExecutionInfo(String.format("%s FAILED, reason: Not implemented",
					testCase.getId()));
			result = null;
		} 
	}

	private static void UpdateStatistics() {		
		caseCount ++;
		if(result == null) {
			failCount ++;
			return;
		}

		if(result.wasSuccessful()) {
			successCount ++;
		} else {
			failCount ++;
		}
	}

	private static void ClearReporter() {
		if(reporter == null) return;
		if(reporter.isClean()) return;
		
		reporter.clear();
	}
	
	private static ExecutionFactory GetExecutionFactory() {
		if(reporter == null || !reporter.isClean()) return null;
		
		ExecutionFactory ef = new ExecutionFactory();
		if(!ef.validationResults.isEmpty()) {
			reporter.addExecutionResult(Exception.SOURCES.get("VALIDATION"), String.format("%s%s", 
					System.lineSeparator(), ef.getPrintableValidationResults()));
		}

		return ef;
	}

}
