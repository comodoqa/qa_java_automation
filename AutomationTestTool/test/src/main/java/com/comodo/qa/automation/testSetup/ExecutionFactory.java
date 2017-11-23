package com.comodo.qa.automation.testSetup;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testRunner.TestRunner;
import com.comodo.qa.automation.testrail.TestRailBuilder;
import com.comodo.qa.browsers.tools.WindowsVersion;

public class ExecutionFactory {
	private static Deserializer deserializer;
    private static StringReader reader;
    private List<ExecutionImport> executionImportList;
    private Execution execution;
    private String importingErrorMessage;
    public List<String> validationResults;
    public static Integer windowsVersion = 0; 
    public static String windowsArchitecture = null;
    
    public ExecutionFactory() {
    	initDeserializer();
    	importingErrorMessage = null;
    	initValidationResults();
    	initWindowsVersion();
    	initWindowsArchitecture();
    }
    
    private void initDeserializer() {
    	deserializer = CsvIOFactory.createFactory(ExecutionImport.class).createDeserializer();
    }
    
    private void initValidationResults() {
    	validationResults = new ArrayList<String>();
    }
    
    private void initWindowsVersion() {
    	if(windowsVersion != 0) return;
    	
    	windowsVersion = WindowsVersion.getWindowsVersion();
    	if(windowsVersion != null) return;
    	
    	String failureReason = "Failed to get Windows version from system info";
		validationResults.add(failureReason); 
    }
    
    private void initWindowsArchitecture() {
    	if(windowsArchitecture != null) return;
    	
    	windowsArchitecture = WindowsVersion.Is64Bit() ? "x64" : "x86";
    }
    
    //region: import from setup files 
    public Execution ImportFromFile(String filePath) throws IOException {
    	execution = new Execution();
    	
    	setReader(filePath);
    	initExecutionImportList();
    	deserialize();
    	validateExecutionImport();
    	removeHeader();
    	mapToExecution(filePath); 
    	
    	return execution;
    }
    
    private void setReader(String filePath) throws IOException {
    	if(filePath == null) {
    		String failureReason = "Missing file path";
    		validationResults.add(failureReason); 
    		return;
    	}
    	
    	String source = new String(Files.readAllBytes(Paths.get(filePath)));
    	if(source != null) {
    		reader = new StringReader(formatSource(source));
    	}
    }
    
    private String formatSource(String source) {
    	source = source.replace("\"", "");
    	source = source.replace(",", ";");
    	
    	return source;
    }
    
    private void initExecutionImportList() {
    	executionImportList = new ArrayList<ExecutionImport>();
    }
    
    private void deserialize() {
    	if(deserializer == null) {
    		String failureReason = "Null deserializer";
    		validationResults.add(failureReason); 
    		return;
    	}
    	if(reader == null) {
    		String failureReason = "Null reader";
    		validationResults.add(failureReason); 
    		return;
    	}
    	
    	deserializer.open(reader);
        
    	while (deserializer.hasNext()) {
    		ExecutionImport executionImport = deserializer.next();
    		executionImportList.add(executionImport);
    	}
    	deserializer.close(true);
    }
    
    private void validateExecutionImport() {
    	if(executionImportList == null || executionImportList.size() < 2) {
    		String failureReason = "Empty import file";
    		execution.validationResults.add(failureReason); 
    	}
    }
    
    private void removeHeader() {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	executionImportList.remove(0);
    }
    
    private void mapToExecution(String filePath) {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	for(ExecutionImport imported : executionImportList){
    		validateSetup(imported);
    		if(!execution.validationResults.isEmpty()) return;
    		setWindowsVersion();
    		setWindowsArchitecture();
    		setExecutionName(filePath);
    		setRunId(imported.runId);
    		setTestCase(imported);
    	}   	
    }
     
    private void validateSetup(ExecutionImport imported) {
    	if(imported == null) {
    		String failureReason = "Missing imported setup";
    		execution.validationResults.add(failureReason);
    		return;
    	}
    	
		validateId(imported.id);
    	validateTitle(imported.title);
		validateCaseId(imported.caseId);
		validateRunConfiguration(imported.runConfiguration);
		validateRunId(imported.runId);
		validateStatus(imported.status);
    }
    
    private void validateId(String id) {
    	if(id == null) {
    		String failureReason = "Missing ID in the setup csv";
    		execution.validationResults.add(failureReason);
    	}else {
    		Pattern caseIdPattern = Pattern.compile("T[0-9]+");
    		if(!caseIdPattern.matcher(id).matches()) {
    			String failureReason = "Invalid ID in the setup csv";
    			execution.validationResults.add(failureReason);
    		}
    	}
    }
    
    private void validateTitle(String title) {
    	if(title != null) return;
    	String failureReason = "Missing title in the setup csv";
    	execution.validationResults.add(failureReason);
    }
    
    private void validateCaseId(String caseId) {
    	if(caseId == null) {
    		String failureReason = "Missing Case ID in the setup csv";
    		execution.validationResults.add(failureReason);
    	}else {
    		Pattern caseIdPattern = Pattern.compile("C[0-9]+");
    		if(!caseIdPattern.matcher(caseId).matches()) {
    			String failureReason = "Invalid Case ID in the setup csv";
    			execution.validationResults.add(failureReason);
    		}
    	}
    }
    
    private void validateRunConfiguration(String runConfiguration) {
    	if(runConfiguration == null) {
    		String failureReason = "Missing run configuration in the setup csv";
    		execution.validationResults.add(failureReason);
    		return;
    	}
    }
    
    private void validateRunId(String runId) {
    	if(runId == null) {
    		String failureReason = "Missing run ID in the setup csv";
    		execution.validationResults.add(failureReason);
    	}else {
    		Pattern caseIdPattern = Pattern.compile("R[0-9]+");
    		if(!caseIdPattern.matcher(runId).matches()) {
    			String failureReason = "Invalid Run ID in the setup csv";
    			execution.validationResults.add(failureReason);
    		}
    	}
    }
   
    private void validateStatus(String status) {
    	if(status != null) return; 
    	String failureReason = "Missing status in the setup csv";
    	execution.validationResults.add(failureReason); 	
    }
    
    private void setWindowsVersion() {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	if(execution.getWindowsVersion() == null) {
    		execution.setWindowsVersion(windowsVersion);
    	}
    }

    private void setWindowsArchitecture() {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	if(execution.getWindowsArchitecture() == null) {
    		execution.setWindowsArchitecture(windowsArchitecture);
    	}
    }
    
    private void setExecutionName(String filePath) {
    	if(!execution.validationResults.isEmpty()) return;
    	if(execution.getName() != null) return;
    	
    	Path p = Paths.get(filePath);
		String fileName = p.getFileName().toString();
		execution.setName(FilenameUtils.removeExtension(fileName));
    }
    
    private void setRunId(String runId) {
    	if(execution.getRunId() == null || execution.getRunId().isEmpty()) {
    		execution.setRunId(runId);
    	} 
    }
    
    private void setTestCase(ExecutionImport imported) {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	TestCase testCase = getTestCase(imported);
    	if(testCase == null) return;
    	
    	execution.addTestCase(testCase);
    }
    
    private TestCase getTestCase(ExecutionImport imported) {
    	String windowsArchitectureInImport = WindowsVersion.Is64Bit(imported.runConfiguration) ? "x64" : "x86";
		Integer windowsVersionInImport = WindowsVersion.getWindowsVersion(imported.runConfiguration);
		
    	if(!isProperWindowsVersion(windowsVersionInImport) || !isProperWindowsArchitecture(windowsArchitectureInImport)) {
    		execution.incrementSkipped();
    		return null;
    	}
    	
    	int statusId = convertStatusToStatusId(imported.status);
		
		if(statusId == 3 || statusId == 4){
    		TestCaseBuilder tcb = new TestCaseBuilder();
    		tcb.setId(imported.id);
    		tcb.setTitle(imported.title);
    		tcb.setCaseId(imported.caseId);
    		tcb.setStatus(statusId);
    		tcb.setExecution(execution);
    		tcb.manageCreateActionList();
    			
    		TestCase testCase = tcb.getTestCase();
    		if(testCase == null || testCase.getActionList() == null || testCase.getActionList().size() == 0) {
        		execution.incrementNotImplemented();
        		return null;
        	}
	    	return testCase;
    	} else {
    		execution.incrementSkipped();
    
    		return null;
    	}  	
    }
    
    private int convertStatusToStatusId(String status) {
    	if(status.contains("Passed")) return 1;
    	if(status.contains("Blocked")) return 2;
    	if(status.contains("Untested")) return 3;
    	if(status.contains("Retest")) return 4;
    	if(status.contains("Failed")) return 5; 
    	
    	return 0;
    } 
    
    private boolean isProperWindowsVersion(Integer windowsVersionInImport) {
    	if(windowsVersionInImport == windowsVersion) return true;
    	
    	return false;
    }
    
    private boolean isProperWindowsArchitecture(String windowsArchitectureInImport) {
    	if(windowsArchitectureInImport.contains(windowsArchitecture)) return true;
		
    	return false;
    }
    
    public String getPrintableValidationResults() {
		String print = String.format("%24s", "");
		for(String message : validationResults) {
			print += message;
		}
		
		if (print.endsWith(String.format(",%s%24s", System.lineSeparator(), ""))) {
			print = print.substring(0, print.length() - (25+(System.lineSeparator()).length()));
		}
		
		return print;
	}
    
    //region: import from testrail
 
    public Execution ImportFromTestRail(String runId) {
    	execution = new Execution();
    	
    	if(!TestSetup.settings.isTestRailImportEnabled()) return execution;
 
    	if(!isProperEnvironment()) return execution;
    		
		JSONArray testCaseList = getTestCasesFromTestRail();
		showImportingErrorMessage();
		if(testCaseList == null || testCaseList.isEmpty()) return execution;
		
		mapToExecution(testCaseList);
    	
    	return execution; 	
    }
    
    private boolean isProperEnvironment() {
    	setWindowsVersion();
		setWindowsArchitecture();
		
    	JSONObject run = getRunsFromTestRail(); 	
		
		String windowsArchitectureInImport = WindowsVersion.Is64Bit(run.get("config").toString()) ? "x64" : "x86";
		Integer windowsVersionInImport = WindowsVersion.getWindowsVersion(run.get("config").toString());
		
		if(!isProperWindowsVersion(windowsVersionInImport) || !isProperWindowsArchitecture(windowsArchitectureInImport)) {
			execution.setSkipped(getTestCaseCount(run));
			
    		String failureReason = String.format(TestSetupStringRepository.VALIDATION_WRONG_ENVIRONMENT, 
    				windowsVersionInImport,
    				windowsArchitectureInImport);
			execution.validationResults.add(failureReason);
    		return false;
    	}
		
		return true;
    }
    
    private int getTestCaseCount(JSONObject run) {
    	int count = 0;
    	count += Integer.parseInt(run.get("retest_count").toString());
    	count += Integer.parseInt(run.get("untested_count").toString());
    	count += Integer.parseInt(run.get("failed_count").toString());
    	count += Integer.parseInt(run.get("passed_count").toString());
    	count += Integer.parseInt(run.get("blocked_count").toString());
    	
    	return count;
    }
    
    private JSONObject getRunsFromTestRail() {
    	TestRailBuilder rb = new TestRailBuilder();
    	JSONObject result = rb.getRun(TestSetup.settings.getRunId());
		
		importingErrorMessage = rb.resultMessage;
		
		return result;
    }
    
    private JSONArray getTestCasesFromTestRail() {
    	TestRailBuilder rb = new TestRailBuilder();
		JSONArray result = rb.getTestCases(TestSetup.settings.getRunId());	
		importingErrorMessage = rb.resultMessage;
		
		return result;
    }
    
    private void mapToExecution(JSONArray testCaseList) {
    	if(!execution.validationResults.isEmpty()) return;
		
		JSONObject _dict = (JSONObject) testCaseList.get(0);
		setExecutionName("R" + _dict.get("run_id").toString());
		setRunId("R" + _dict.get("run_id").toString());
		
    	for (int i = 0; i < testCaseList.size(); i++) {
			JSONObject dict = (JSONObject) testCaseList.get(i);
  		
    		setTestCase(dict);
    	}   	
    }
    
    private void setTestCase(JSONObject dict) {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	TestCase testCase = getTestCase(dict);
    	if(testCase == null) return;
    	
    	execution.addTestCase(testCase);
    }
    
    private TestCase getTestCase(JSONObject dict) {
    	int statusId = Integer.parseInt(dict.get("status_id").toString());
			
			if(statusId == 3 || statusId == 4) {
	    		TestCaseBuilder tcb = new TestCaseBuilder();
	    		tcb.setId("T" + dict.get("id").toString());
	    		tcb.setTitle(dict.get("title").toString());
	    		tcb.setCaseId("C" + dict.get("case_id").toString());
	    		tcb.setStatus(statusId);
	    		tcb.setExecution(execution);
	    		tcb.manageCreateActionList();
	    			
	    		TestCase testCase = tcb.getTestCase();
	    		if(testCase == null || testCase.getActionList() == null || testCase.getActionList().size() == 0) {
	        		execution.incrementNotImplemented();
	        		return null;
	        	}
	    		
	    		return testCase;
			} else {
	    		execution.incrementSkipped();
	    		
	    		return null;
	    	}  	
    }
    
    private void showImportingErrorMessage(){
		if(importingErrorMessage == null || importingErrorMessage.isEmpty()) return;
		
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.INFOLOGS_FAILED_TO_IMPORT_FROM_TESTRAIL, 
						importingErrorMessage));
	}
	
}
