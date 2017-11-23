package com.comodo.qa.automation.actions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;
import com.comodo.qa.browsers.tools.WindowsRegistry;

public class CHECK_REGISTRY_KEY_VALUE {
	private String className = null;
	private Map<String, String> parameters = null;
	private boolean isResultNegated = false;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	
	private boolean isLocalMachine = true;
	private String key = null;
	private String value = null;
	private String expectedValue = null; 
	
	public CHECK_REGISTRY_KEY_VALUE() {
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
		String actualValue = getRegistryKey();
		if(actualValue == null || actualValue.isEmpty() || !actualValue.contains(expectedValue)) {
			isPassed = false;
		}
		//---End Test
		if(isResultNegated) {
			isPassed = !isPassed;
		}
		setAssertionMessage();
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
	
	private Boolean isValidParameters() {
		//---isLocalMachine
		int i = 1;
		String _isLocalMachine = null;
		while((_isLocalMachine == null || _isLocalMachine.isEmpty()) && i < 1000) {
			_isLocalMachine = parameters.get("isLocalMachine" + i);
			i++;
		}
		i--;
		if(_isLocalMachine == null || _isLocalMachine.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [isLocalMachine%s]", className, i);
			return false;
		}
		isLocalMachine = Boolean.parseBoolean(_isLocalMachine);
		//---key
		key = parameters.get("key" + i);
		if(key == null || key.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [key%s]", className, i);
			return false;
		}
		//---value
		value  = parameters.get("value" + i);
		if(value == null || value.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [value%s]", className, i);
			return false;
		}
		//---expectedValue
		expectedValue  = parameters.get("expectedValue" + i);
		if(expectedValue == null || expectedValue.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [expectedValue%s]", className, i);
			return false;
		}
		//---failMessage
		String _isResultNegated = parameters.get("isNegateResult" + i);
		if(_isResultNegated == null || _isResultNegated.isEmpty()) {//false
			failMessage  = String.format("%s [in %s]", parameters.get("failMessage" + i), className);
			if(failMessage == null || failMessage.isEmpty()) {
				failMessage = String.format("Invalid parameters in %s: [failMessage%s]", className, i);
				return false;
			}
		} else {//true
			String altFailMessage = String.format("%s [in %s]", parameters.get("altFailMessage" + i), className);
			if(altFailMessage == null || altFailMessage.isEmpty()) {
				failMessage = String.format("Invalid parameters in %s: [altFailMessage%s]", className, i);
				return false;
			}
			failMessage = altFailMessage;
		    isResultNegated = Boolean.parseBoolean(_isResultNegated);
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
	
	private String getRegistryKey() {
		try {
			return WindowsRegistry.readString(
				(isLocalMachine ? WindowsRegistry.HKEY_LOCAL_MACHINE : WindowsRegistry.HKEY_CURRENT_USER), 
				key, 
				value);
		} catch (IllegalArgumentException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		} catch (IllegalAccessException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		} catch (InvocationTargetException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		}
		
		return null;
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
	
	private void printDebugging() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(String.format("Executing %s: %s//%s", className, (isLocalMachine ? "HKEY_LOCAL_MACHINE" : "HKEY_CURRENT_USER"), key, value));
	}
	
	private void printResults() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(String.format("%s result: ", className) + (isPassed ? "PASSED" : "FAILED #" + failMessage));
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
