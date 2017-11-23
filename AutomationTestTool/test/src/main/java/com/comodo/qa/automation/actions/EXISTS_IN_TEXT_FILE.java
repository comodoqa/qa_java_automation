package com.comodo.qa.automation.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Scanner;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;

public class EXISTS_IN_TEXT_FILE implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private boolean isResultNegated = false;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	
	private String filePath = null;
	private String text = null;
	
	public EXISTS_IN_TEXT_FILE() {
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
		existsTextInFile();
		//---End Test
		if(isResultNegated) {
			isPassed = !isPassed;
		}
		setAssertionMessage();
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
			
	private void existsTextInFile() {
		File file = new File(filePath);
		
		Scanner scanner = null;
		try {
		    scanner = new Scanner(file);

		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        if(line.contains(text)) { 
		            isPassed = true;
		            return;
		        }
		    }
		} catch(FileNotFoundException e) { 
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		} finally {
			scanner.close();
		}
		
		isPassed = false;
	}
	
	//---recurring methods
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
			text  = parameters.get("text" + i);
			i++;
		}
		if(text == null || text.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [text%s]", className, i);
			return false;
		}
		i--;
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
		//---isAssert
		String _isAssert = parameters.get("isAssert" + i);
		if(_isAssert != null && !_isAssert.isEmpty()) {
			isAssert = Boolean.parseBoolean(_isAssert);
		}
		//---filePath
		filePath = parameters.get("filePath" + i);
		if(filePath == null || filePath.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [filePath%s]", className, i);
			return false;
		}
		//---text
		String text = parameters.get("text" + i);
		if(text == null || text.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [text%s]", className, i);
			return false;
		}
		//---
		return true;
	}
	
	private void printDebugging() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(String.format("Executing %s: %s", className, text));
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
