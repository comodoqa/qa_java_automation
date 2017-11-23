package com.comodo.qa.automation.actions;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.comodo.qa.automation.testSetup.Settings;
import com.comodo.qa.automation.testSetup.TestSetup;

public class EXISTS_IN_COMODO_BROWSER_HISTORY implements IAction{
	private String className = null;
	private Map<String, String> parameters = null;
	private Boolean isPassed = null;
	private Boolean isAssert = null;
	private String failMessage = null;
	private Boolean isOptional = null;
	private String expectedValue = null;
	
	private boolean isPortable = false;
	private Boolean isCIDProvided = null;
	
	public EXISTS_IN_COMODO_BROWSER_HISTORY() {
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
		//---Test
		setExpectedValue();
		printDebugging();
		List<String> history = getHistory();
		isPassed = false;
		if(history == null || history.size() == 0) {
			failMessage = "Failed to retrieve browser history";
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
			return isPassed;
		}
		for (String item : history) {
			if(item.compareToIgnoreCase(expectedValue) == 0) {
				isPassed = true;
				break;
			}
		}
		if(!isPassed) {
			failMessage = String.format(failMessage,
					expectedValue,
					history);
		}
		//---End Test
		setAssertionMessage();
		printResults();
		setOptionalResult();
		
		return isPassed;
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
		//---expectedValue
		int i = 1;
		while((expectedValue == null || expectedValue.isEmpty()) && i < 1000) {
			expectedValue = parameters.get("expectedValue" + i);
			i++;
		}
		i--;
		if(expectedValue == null || expectedValue.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [expectedValue%s]", className, i);
			return false;
		}
		//---isPortable
		String _isPortable = parameters.get("isPortable" + i);
		if(_isPortable == null || _isPortable.isEmpty()) {
			failMessage = String.format("Invalid parameters in %s: [isPortable%s]", className, i);
			return false;
		} else {
			isPortable = Boolean.parseBoolean(_isPortable);			
		}
		//---failMessage
		failMessage  = String.format("%s \n[in %s]", parameters.get("failMessage" + i), className);
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
		//---isCIDProvided
		String _isCIDProvided = parameters.get("isCIDProvided" + i);
		if(_isCIDProvided != null && !_isCIDProvided.isEmpty()) {
			isCIDProvided = Boolean.parseBoolean(_isCIDProvided);
		}
		//---
		return true;
	}
	
	private void setExpectedValue() {
		String _expectedValue = null;
		if(expectedValue.contains("yahoo")) {
			if(isCIDProvided == null || isCIDProvided == false) {
				_expectedValue = String.format(expectedValue,
						TestSetup.settings.getProductId(),
						TestSetup.settings.getChannelId(),
						TestSetup.settings.getVersion());
			} else {
				_expectedValue = String.format(expectedValue,
						TestSetup.settings.getProductId(),
						TestSetup.settings.getVersion());
			}
			
			expectedValue = _expectedValue;
		} 
	}
	
	public List<String> getHistory() {
		Connection connection = null;
		List<String> urlList = null;
		try {
			if (isPortable) {
				connection = DriverManager.getConnection("jdbc:sqlite:" 
						+ "C:\\Test\\Comodo\\" 
						+ TestSetup.settings.getBrowser() 
						+ "\\User Data\\Default\\History");
			} else {
				connection = DriverManager.getConnection("jdbc:sqlite:" 
						+ System.getProperty("user.home")
						+ "\\AppData\\Local\\Comodo\\" 
						+ TestSetup.settings.getBrowser() 
						+ "\\User Data\\Default\\History");
			}
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM urls");
			
			urlList = new ArrayList<String>();
			while (rs.next()) {
				String urls = rs.getString("url");
				urlList.add(urls);
			}
		} catch (SQLException e) {
			failMessage = e.getMessage();
			Action.SetAssertionMessage(true, failMessage);
			isPassed = false;
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				failMessage = e.getMessage();
				Action.SetAssertionMessage(true, failMessage);
				isPassed = false;
			}
		}

		return urlList;
	}
	
	private void printDebugging() {
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		System.out.println(String.format("Executing %s: %s", className, expectedValue));
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
