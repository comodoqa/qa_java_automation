package com.comodo.qa.browsers.testsetup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Execution {
	private String name;
	private Integer windowsVersion;
	private String windowsArchitecture;
	private List<TestCase> testCases;
	private Date timestamp; 
	private int skipped;
	public List<String> validationResults;
	private String version;
	
	public Execution(){
		testCases = new ArrayList<TestCase>();
		skipped = 0;
		validationResults =  new ArrayList<String>();
	}
	
	public String getName() {
		return this.name;
	}
	 
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getWindowsVersion() {
		return this.windowsVersion;
	}
	 
	public void setWindowsVersion(int windowsVersion) {
		this.windowsVersion = windowsVersion;
	}
	
	public String getWindowsArchitecture() {
		return this.windowsArchitecture;
	}
	 
	public void setWindowsArchitecture(String windowsArchitecture) {
		this.windowsArchitecture = windowsArchitecture;
	}
	
	public List<TestCase> getTestCases() {
		return this.testCases;
	}
	 
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = new ArrayList<TestCase>(testCases);
	}
	
	public void addTestCase(TestCase testCase) {
		if(testCase == null) return;
		if(this.testCases == null) return;
		
		this.testCases.add(testCase);
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	 
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getFormattedTimestamp() {
		if(this.timestamp == null) return null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		return dateFormat.format(this.timestamp);
	}
	
	public void incrementSkipped() {
		this.skipped ++;
	}
	
	public int getSkipped() {
		return this.skipped;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getPrintableValidationResults() {
		String print = String.format("%24s", "");
		for(String message : validationResults)
		{
			print += String.format("%s,%s%24s", message, System.lineSeparator(), "");
		}
		
		if (print.endsWith(String.format(",%s%24s", System.lineSeparator(), ""))) {
			print = print.substring(0, print.length() - (25+(System.lineSeparator()).length()));
		}
		
		return print;
	}
	
}
