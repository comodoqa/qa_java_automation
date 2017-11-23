package com.comodo.qa.automation.actions;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sikuli.basics.Settings;
import org.sikuli.script.Screen;
import org.sikuli.script.TextRecognizer;

import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testRunner.TestCaseRunner;
import com.comodo.qa.automation.testRunner.TestRunner;
import com.comodo.qa.automation.testSetup.ExecutionFactory;

public class Action implements Comparable<Action>{
	private int orderId;
	private String type;
	private Map<String, String> parameters;
	public static Screen screen;
	
	public Action() {
		parameters = new HashMap<String, String>();
	}
	
	public int getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Object orderId) {
		int _orderId = 0;
		if(orderId != null) {
			try{
				_orderId = Integer.parseInt(orderId.toString());
			} catch (NumberFormatException e){
				_orderId = 0;
			}
		}
		this.orderId = _orderId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(Object type) {
		if(type == null) {
			this.type = "";
			return;
		}
		
		this.type = type.toString();
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public boolean isValidAction() {
		if(!isValidType()) return false;
		if(!isValidOrderId()) return false;
		if(!isValidParameters()) return false;
		if(!isValidTemplateName()) return false;
		
		return true;
	}
	
	private boolean isValidType() {
		if(this.type != null && !this.type.isEmpty()) return true;
		
		TestRunner.loggers.appendError( 
				String.format(TestSetupStringRepository.ERRORLOGS_MISSING_PARAMETER,
						"type"));
		TestCaseRunner.validationMessage = "missing or invalid [type]";
		
		return false;
	}
	
	private boolean isValidOrderId() {
		if(orderId > 0) return true;
		
		TestRunner.loggers.appendError( 
				String.format(TestSetupStringRepository.ERRORLOGS_MISSING_PARAMETER,
						"orderId"));
		
		TestCaseRunner.validationMessage = "missing or invalid [orderId]";
		return false;
	}
	
	private boolean isValidParameters() {
		if(this.parameters != null && this.parameters.size() > 0) return true;
		
		String.format(TestSetupStringRepository.ERRORLOGS_MISSING_PARAMETERS,
				this.orderId + ":" + this.type);
		TestCaseRunner.validationMessage = "missing or invalid parameter list";
		
		return false;
	}
	
	private boolean isValidTemplateName() {
		if(!this.type.contains("TEMPLATE")) return true;
		
		String templateName = this.parameters.get("templateName");
		
		if(templateName != null && !templateName.isEmpty()) return true;
		
		TestRunner.loggers.appendError( 
				String.format(TestSetupStringRepository.ERRORLOGS_MISSING_PARAMETER, 
						"templateName"));
		TestRunner.loggers.appendInfo( 
				String.format(TestSetupStringRepository.ERRORLOGS_MISSING_PARAMETER,
						"templateName"));
		
		TestCaseRunner.validationMessage = "missing or invalid template name";
		return false;
	}
	
	public int compareTo(Action o) {
		return o.getOrderId() > this.getOrderId() ? -1 : 1;
	}
	
	public static void SetAssertionMessage(Boolean isAssert, String message) {
		if(isAssert == null || isAssert == false) return;
		
		TestCaseRunner.assertFailMessage = message;	
	}
	
	public static JSONArray GetActionParameters (JSONObject dict) {
		String parameterRepositoryName = String.format("%s_parameters", ExecutionFactory.windowsArchitecture);
		JSONArray jsonParameters = null;
		jsonParameters = (JSONArray) dict.get(parameterRepositoryName);
		if(jsonParameters == null) {
			jsonParameters = (JSONArray) dict.get("parameters");
		}
		
		return jsonParameters;
	}
	
	public static void InitSikuli() {
		Settings.UserLogs = false;
		Settings.ActionLogs = false;
		Settings.InfoLogs = false;
		Settings.MoveMouseDelay = 0;
		Settings.OcrTextSearch = true;
		Settings.OcrTextRead = true;
		Settings.OcrLanguage = "eng";
		Settings.MinSimilarity = 0.99;
		TextRecognizer.reset();
		
		if(screen == null) screen = new Screen();
	}	
	
}
