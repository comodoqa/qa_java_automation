package com.comodo.qa.automation.actions;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.sikuli.script.FindFailed;

import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;

public class FIND implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	
	private String image = null;
	private double timeout = 0;
	
	public FIND (){
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
		doFind();	
		//---End Test
		setAssertionMessage();		
		printResults();
		setOptionalResult();
		
		return isPassed;
	}
			
	private void doFind() {
		if(Action.screen == null) Action.InitSikuli();
		
		String fullImageUrl = getFullImageUrl();
		if(!isFileExist(fullImageUrl)) {
			failMessage = String.format("Image [%s.png] does not exist", image);
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
			return;
		}
		
		try {
			Action.screen.wait(fullImageUrl, (timeout / 1000));
		} catch (FindFailed e) {
			isPassed = false;
			return;
		}
	}
	
	private String getFullImageUrl() {
		String path = String.format(TestSetupStringRepository.PATTERN_IMAGE_PATH, 
				TestSetup.resourceFolder.getPath(), 
				TestSetupStringRepository.IMAGES_FOLDER_NAME,
				image);
				
		return path;
	}
	
	private boolean isFileExist(String filepath) {
		File file = new File(filepath);
		if(!file.exists()) {
			return false;
		}
		
		return true;
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
		//---image
		int i = 1;
		while((image == null || image.isEmpty()) && i < 1000) {
			image = parameters.get("image" + i);
			i++;
		}
		i--;
		if(image == null || image.isEmpty()) {
			image = String.format("Invalid parameters in %s: [image%s]", className, i);
			return false;
		}
		//---failMessage
		failMessage  = String.format("%s [in %s]", parameters.get("failMessage" + i), className);
		if(failMessage == null || failMessage.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [failMessage%s]", className, i);
			return false;
		}
		//--timeout
		String _timeout =  parameters.get("timeout" + i);
		if(_timeout == null || _timeout.isEmpty()) {
			image = String.format("Invalid parameters in %s: [timeout%s]", className, i);
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
		
		System.out.println(String.format("Executing %s: %s(%.1fs)", className, image, (timeout / 1000.0)));
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
