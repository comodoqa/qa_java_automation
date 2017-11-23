package com.comodo.qa.automation.actions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sikuli.api.robot.Key;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;

public class TYPE_COMBO implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	
	private List<String> holdDown = null;
	private String key = null;
	
	public TYPE_COMBO() {
		super();
		holdDown = new ArrayList<String>();
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
		doTypeCombo();
		//---End Test
		setAssertionMessage();
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
	
	private void doTypeCombo() {
		if(Action.screen == null) Action.InitSikuli();
		for(String key1 : holdDown) {
			Action.screen.keyDown(key1);
		}
		Action.screen.type(key);
		Action.screen.keyUp();
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
		//---holdDown
		String _holdDownList = null;
		int i = 1;
		while((_holdDownList == null || _holdDownList.isEmpty()) && i < 1000) {
			_holdDownList  = parameters.get("holdDown" + i);
			i++;
		}
		i--;
		if(_holdDownList == null || _holdDownList.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [holdDown%s]", className, i);
			return false;
		}
		String[] _holdDown = _holdDownList.split(",");
		for (String key : _holdDown) {
			System.out.println(key);
			String _key = getKey(key);
			holdDown.add(_key);
		}
		//---key
		key  = parameters.get("key" + i);
		if(key == null || key.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [key%s]", className, i);
			return false;
		}
		key = getKey(key);
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
	
	private String getKey(String pattern) {
		Key key = new Key();
		String keyValue = null;
		try {
			keyValue = key.getClass().getField(pattern).get(key).toString();
		} catch (IllegalArgumentException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		} catch (IllegalAccessException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		} catch (NoSuchFieldException e) {
			keyValue = pattern;
		} catch (SecurityException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		}
		
		return keyValue;
	}
	
	private void printDebugging() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(String.format("Executing %s: %s + %s", className, holdDown, key));
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
