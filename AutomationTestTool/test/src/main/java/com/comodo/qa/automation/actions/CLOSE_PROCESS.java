package com.comodo.qa.automation.actions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;
import com.comodo.qa.browsers.tools.ProcessHelper;

public class CLOSE_PROCESS implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;

	private String processName = null;
	private long timeout = 0;
	private long wait = 1000;
	
	private boolean processExists = false;
	
	public CLOSE_PROCESS() {
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
		timeout *= TestSetup.settings.getMultiplier();
		printDebugging();
		//---Test
		setProcessExists();
		closeProcess();
		//---End Test
		setAssertionMessage();		
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
	
	private void setProcessExists() {
		long intervals = timeout / wait;
		for(int i = 0; i < intervals; i++) {
			try {
				Thread.sleep(wait);
				processExists = ProcessHelper.isProcessRunning(processName);
			} catch (IOException e) {
				failMessage = e.getMessage();
				Action.SetAssertionMessage(true, failMessage);
				isPassed = false;
			} catch (InterruptedException e) {
				failMessage = e.getMessage();
				Action.SetAssertionMessage(true, failMessage);
				isPassed = false;
			}
			
			if(processExists) break;
		}
	}
	
	private void closeProcess() {
		String command = "taskkill /F /IM " + processName;
		if(processExists) {
			try {
			Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				failMessage = e.getMessage();
				Action.SetAssertionMessage(true, failMessage);
				isPassed = false;
			}
		} else {
			isPassed = false;
			failMessage = String.format("Process %s does not exist", processName);
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
		//---processName
		int i = 1;
		while((processName == null || processName.isEmpty()) && i < 1000) {
			processName  = parameters.get("processName" + i);
			i++;
		}
		i--;
		if(processName == null || processName.isEmpty()) {
			processName = String.format("Invalid parameters in %s: [processName" + i + "]", className);
			return false;
		}
		//---timeout
		String _timeout = parameters.get("timeout" + i);
		if(_timeout == null || _timeout.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [timeout%s]", className, i);
			return false;
		}
		timeout = Long.parseLong(_timeout);
		//---failMessage
		failMessage  = String.format("%s: %s[in %s]", parameters.get("failMessage" + i), processName, className);
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
		
		System.out.println(String.format("Executing %s: %s", className, processName));
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
