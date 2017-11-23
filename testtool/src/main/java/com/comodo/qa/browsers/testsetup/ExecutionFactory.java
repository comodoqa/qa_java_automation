package com.comodo.qa.browsers.testsetup;

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

import com.comodo.qa.browsers.tools.WindowsVersion;

public class ExecutionFactory {
	private static Deserializer deserializer;
    private static StringReader reader;
    private List<ExecutionImport> executionImportList;
    private Execution execution;
    public List<String> validationResults;
    private Integer windowsVersion; 
    private String windowsArchitecture;
    
    public ExecutionFactory() {
    	initDeserializer();
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
    	windowsVersion = WindowsVersion.getWindowsVersion();
    	if(windowsVersion != null) return;
    	
    	String failureReason = "Failed to get Windows version from system info";
		validationResults.add(failureReason); 
    }
    
    private void initWindowsArchitecture() {
    	windowsArchitecture = WindowsVersion.Is64Bit() ? "x64" : "x86";
    }
    
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
		validateRun(imported.run);
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
    		String failureReason = "Missing case ID in the setup csv";
    		execution.validationResults.add(failureReason);
    	}else {
    		Pattern caseIdPattern = Pattern.compile("C[0-9]+");
    		if(!caseIdPattern.matcher(caseId).matches()) {
    			String failureReason = "Invalid case ID in the setup csv";
    			execution.validationResults.add(failureReason);
    		}
    	}
    }
    
    private void validateRun(String run) {
    	if(run == null) {
    		String failureReason = "Missing run in the setup csv";
    		execution.validationResults.add(failureReason);
    		return;
    	}
		String windowsArchitectureInImport = WindowsVersion.Is64Bit(run) ? "x64" : "x86";
		Integer windowsVersionInImport = WindowsVersion.getWindowsVersion(run);
		
		validateRunUnique(windowsVersionInImport);
    	validateRunVersionFromImport(windowsVersionInImport);
    	validateRunArchitectureFromImport(windowsArchitectureInImport);
    }
    
    private void validateRunUnique(Integer windowsVersionInImport) {
    	if(execution.getWindowsVersion() != null && execution.getWindowsVersion() != windowsVersionInImport) {
			String failureReason = "Distinct Run in same execution import";
			execution.validationResults.add(failureReason);
    	}
    }
    
    private void validateRunVersionFromImport(Integer windowsVersionInImport) {
    	if(windowsVersionInImport == windowsVersion) return;
    	
    	String failureReason = String.format("Windows version from import [%s] different from version of running OS:[%s]",
    			windowsVersionInImport, windowsVersion);
		execution.validationResults.add(failureReason);
    }
    
    private void validateRunArchitectureFromImport(String windowsArchitectureInImport) {
    	if(windowsArchitectureInImport.contains(windowsArchitecture)) return;
		
    	String failureReason = String.format("Windows architecture from import [%s] different from architecture of running OS:[%s]",
    			windowsArchitectureInImport, windowsArchitecture);
		execution.validationResults.add(failureReason);
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
    
    private void setTestCase(ExecutionImport imported) {
    	if(!execution.validationResults.isEmpty()) return;
    	
    	TestCase testCase = getTestCase(imported);
    	execution.addTestCase(testCase);
    }
    
    private TestCase getTestCase(ExecutionImport imported) {
    	if(imported.status.contains("Untested") || imported.status.contains("Retest")) {
	    	TestCase testCase = new TestCase();
	    	testCase.setId(imported.id);
	    	testCase.setTitle(imported.title);
	    	testCase.setCaseId(imported.caseId);
	    	testCase.setStatus(imported.status);
	    	return testCase;
    	}else {
    		execution.incrementSkipped();
    		return null;
    	}  	
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
    
}
