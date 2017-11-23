package com.comodo.qa.automation.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;

public class CLEAR_USER_DATA implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	
	private String path = null;
	
	public CLEAR_USER_DATA() {
		super();
		className = getClassName();
		isPassed = true;
	}
	
	public Boolean run(Map<String, String> parameters) {
		this.parameters = parameters;
		setParametersFromSettings();
		
		isPassed = isValidParameters();
		if(!isPassed) {
			Action.SetAssertionMessage(true, failMessage);
			return isPassed;
		}
		printDebugging();
		//---Test
		deleteAppData();	
		//---End Test
		setAssertionMessage();		
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
	
	public void deleteAppData() {
		String userHome = System.getProperty("user.home");

		String[] paths = {
				String.format("%s\\AppData\\Roaming\\%s", userHome, path),
				String.format("%s\\AppData\\Local\\%s", userHome, path)};
		for(String path : paths) {
			File file = new File(path);
			try {  
				FileUtils.deleteDirectory(file);  
			} catch (IOException e) {  
				failMessage = e.getMessage();
				Action.SetAssertionMessage(true, failMessage);
				isPassed = false;
			}  
		}
	} 
	
	//--recurring methods
	private void setParametersFromSettings() {
		for(Map.Entry<String, String> parameter : parameters.entrySet()) {
			if(parameter.getValue().contains("##")) {
				Field[] settings = Settings.class.getDeclaredFields();
				for(Field field : settings) {
					String cleanedValue = parameter.getValue().replace("##", "");
					if(cleanedValue.contains(field.getName())) {
						try {
							String _parameter = parameter.getValue();
							_parameter = _parameter.replace("##" + field.getName(), field.get(TestSetup.settings).toString());
							parameter.setValue(_parameter);
						} catch (IllegalArgumentException e) {
							failMessage = e.getMessage();
							Action.SetAssertionMessage(true, failMessage);
							isPassed = false;
						} catch (IllegalAccessException e) {
							failMessage = e.getMessage();
							Action.SetAssertionMessage(true, failMessage);
							isPassed = false;
						}
					}
				}
			}
		}
	}
	
	private Boolean isValidParameters() {
		//---path
		int i = 1;
		while((path == null || path.isEmpty()) && i < 1000) {
			path  = parameters.get("path" + i);
			i++;
		}
		i--;
		if(path == null || path.isEmpty()) {
			path = String.format("Invalid parameters in %s: [path" + i + "]", className);
			return false;
		}
		//---failMessage
		failMessage  = String.format("%s [in %s]", parameters.get("failMessage" + i), className);
		if(failMessage == null || failMessage.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [failMessage%s]", className, i);
			return false;
		}
		//---isOptional
			String _isOptional = parameters.get("isOptional" + i);
			if(_isOptional != null && !_isOptional.isEmpty()) {
				isOptional = Boolean.parseBoolean(_isOptional);
			}
		//---isAssert
		String _isAssert = parameters.get("isAssert" + i);
		if(_isAssert != null && !_isAssert.isEmpty()) {
			isAssert = Boolean.parseBoolean(_isAssert);
		}
		//---
		return true;
	}
	
	private void printDebugging() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(String.format("Executing %s", className));
	}
	
	private void printResults() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(className + " result: " + (isPassed ? "PASSED" : "FAILED #" + failMessage));
		System.out.println("");
	}
	
	public void setAssertionMessage() {
		if(isPassed) return;

		Action.SetAssertionMessage(isAssert, failMessage);
	}
	
	private String getClassName() {
		String fullClassName = this.getClass().getName();
		
		return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
	}

	private void setOptionalResult() {
		if(isOptional == null) return;
		
		if(isOptional) isPassed = true;
	}
	
}
