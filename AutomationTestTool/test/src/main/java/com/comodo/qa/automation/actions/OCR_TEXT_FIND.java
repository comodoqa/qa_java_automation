package com.comodo.qa.automation.actions;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.util.Map;

import org.sikuli.script.Region;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;

public class OCR_TEXT_FIND implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	private int rescan = 2;
	
	private String text = null;
	private double timeout = 0;
	
	public OCR_TEXT_FIND (){
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
		doOcrFind();	
		//---End Test
		setAssertionMessage();		
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
			
	private void doOcrFind() {
		boolean isFound = false;
		
		try {
			Thread.sleep((long) timeout);
		} catch (InterruptedException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		}
		
		for(int i = 0; i < rescan; i++) {
			if(Action.screen == null) Action.InitSikuli();
			
			Region region = getFullScreenRegion();
			String _text = region.text();
			_text = _text.replace("\n", "");
			_text = _text.replace("  ", " ");
			
			System.out.println();System.out.println();
			System.out.println("text found: " + _text);
			System.out.println();System.out.println();
			System.out.println("SCANNING FOR TEXT ... " + (i+1));
			
			if(_text.contains(text)) {
				isFound = true;
				break;
			}
		}
		
		if(!isFound) {
			failMessage = String.format("Text [%s] not found", text);
			isPassed = false;
		}
	}
	
	private Region getFullScreenRegion() {
		Region region = null;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		region = Region.create(0, 0, (int) width, (int) height);
		
		return region;
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
		//---text
		int i = 1;
		while((text == null || text.isEmpty()) && i < 1000) {
			text = parameters.get("text" + i);
			i++;
		}
		i--;
		if(text == null || text.isEmpty()) {
			text = String.format("Invalid parameters in %s: [text%s]", className, i);
			return false;
		}
		//--timeout
		String _timeout =  parameters.get("timeout" + i);
		if(_timeout == null || _timeout.isEmpty()) {
			text = String.format("Invalid parameters in %s: [text%s]", className, i);
			return false;
		}
		try {
			timeout = Double.parseDouble(_timeout);
		} catch(NumberFormatException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
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
		
		System.out.println(String.format("Executing %s: %s(%.1fs)", className, text, (timeout / 1000.0)));
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
