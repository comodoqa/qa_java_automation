package com.comodo.qa.automation.actions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;
import com.comodo.qa.browsers.tools.ProcessHelper;

public class WAIT_FOR_PROCESS_TO_FINISH implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	
	private long timeout = 0;
	private long wait = 1000;
	private String processName = null;
	
	public WAIT_FOR_PROCESS_TO_FINISH() {
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
		waitForProcessToFinish();
		//---End Test
		setAssertionMessage();		
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
			
	private void waitForProcessToFinish() {
		long intervals = timeout / wait;
		boolean processExists = false;
		
		try {
			if(!ProcessHelper.isProcessRunning(processName)) {
				failMessage = "Process is not running";
				isPassed = false;
			}
			
			for(int i = 0; i < intervals; i++) {
				try {
					Thread.sleep(wait);
					processExists = ProcessHelper.isProcessRunning(processName);
				} catch (IOException e) {
					failMessage = e.getMessage();
					isPassed = false;
				} catch (InterruptedException e) {
					failMessage = e.getMessage();
					isPassed = false;
				}
				
				if(!processExists) break;
			}
		} catch (IOException e) {
			failMessage = e.getMessage();
			isPassed = false;
		} catch (InterruptedException e) {
			failMessage = e.getMessage();
			isPassed = false;
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
		//---timeout
		int i = 1;
		String _timeout = null;
		while((_timeout == null || _timeout.isEmpty()) && i < 1000) {
			_timeout  = parameters.get("timeout" + i);
			i++;
		}
		i--;
		if(_timeout == null || _timeout.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [timeout%s]", className, i);
			return false;
		}
		timeout = Long.parseLong(_timeout);
		//---failMessage
		failMessage  = String.format("%s \n[in %s]", parameters.get("failMessage" + i), className);
		if(failMessage == null || failMessage.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [failMessage%s]", className, i);
			return false;
		}
		//---processName
		processName = parameters.get("processName" + i);
		if(processName == null || processName.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [processName%s]", className, i);
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
		
		System.out.println(String.format("Executing %s: %s(%.1fs)", className, processName, (timeout / 1000.0)));
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
