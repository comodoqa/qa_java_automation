package com.comodo.qa.automation.testRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comodo.qa.automation.actions.Action;
import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testSetup.TestSetup;
import com.comodo.qa.browsers.tools.FileHelper;

public class TemplateRunner {
	private String templateName = null;
	private Map<String, String> parameters = null;
	private List<Action> actionList = null;
	private Action action = null;
	
	private File templateFile;
	public static JSONParser jsonParser = null;
	public static JSONObject jsonObject = null;
	public static JSONArray jsonActionList = null;
	
	public TemplateRunner(String templateName, Map<String, String> parameters) {		
		this.templateName = templateName;
		this.parameters = parameters;	
	}
	
	private boolean isValidParametersList(Map<String, String> _parameters) {
		if(_parameters == null || _parameters.size() == 0) {		
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_INVALID_TEMPLATE_PARAMETERS));
			return false;
		}
		
		return true;
	}
	
	public Boolean execute() {
		if(!isValidParametersList(parameters)) return false;
				
		setTestSetupFile(templateName);
		FileReader reader = getFileReader();
		if(reader == null) return false;
		
		setJsonObject(reader);
		if(jsonObject == null) return false;	
		
		manageTemplateMapping();
		if(actionList == null || actionList.size() == 0) return false;
		runTemplate();
		
		return true;
	}
	
	private void setTestSetupFile(String fileName) {
		String rootPath = String.format(TestSetupStringRepository.PATTERN_IMPLEMENTATION_ROOT_PATH, 
				TestSetup.resourceFolder.getPath(), 
				TestSetupStringRepository.IMPLEMENTATIONS_FOLDER_NAME);
		
		String path = String.format("%s.json", fileName);
		templateFile = FileHelper.getFileFromRootFolder(rootPath, path);
	}
	
	private FileReader getFileReader() {
		FileReader reader = null;

		if(templateFile == null) {
			reader = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_IMPLEMENTATION_FILE_READ_MESSAGE, 
							templateName));
			return reader;
		}
		
		try {
			reader = new FileReader(templateFile);
			
			TestRunner.loggers.appendDebug(
					String.format(TestSetupStringRepository.INFOLOGS_TEMPLATES_FILE_MESSAGE, 
							templateFile));
		} catch (FileNotFoundException e) {
			reader = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_IMPLEMENTATION_FILE_READ_MESSAGE, 
							templateFile.getPath()));
			TestCaseRunner.assertFailMessage = String.format("Template [%s] not implemented", templateName);
		}
		
		return reader;
	}
	
	private void setJsonObject(FileReader reader) {
		jsonParser = new JSONParser();
		try {
			jsonObject = (JSONObject) jsonParser.parse(reader);
		} catch (IOException e) {
			jsonObject = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_FILE_READ_MESSAGE, 
							templateFile.getPath()));
		} catch (ParseException e) {
			jsonObject = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_PARSE_MESSAGE, 
							templateFile.getPath()));
		}
	}
	
	private void manageTemplateMapping() {
		actionList = new ArrayList<Action>();
		setActionList();
		if(jsonActionList == null) return;
		
		for (int i = 0; i < jsonActionList.size(); i++) {
			Action action = getAction(i);
			if(action == null) {
				actionList = null;
				return;
			}
			actionList.add(action);
		}
		
		TestRunner.loggers.appendDebug(
				String.format(TestSetupStringRepository.INFOLOGS_TEMPLATE_IMPORTED_MESSAGE, 
						templateName));
	}

	private void setActionList() {
		jsonActionList = (JSONArray) jsonObject.get("actions");
	}
	
	private Action getAction(int i) {
		action = new Action();
		
		JSONObject dict = (JSONObject) jsonActionList.get(i);
		action.setOrderId(dict.get("orderId"));
		action.setType(dict.get("type"));
		Map<String, String> parameters = getActionParameters(dict);
		if(parameters == null) return null;
		
		action.setParameters(parameters);
			
		if(!action.isValidAction()){
			return null;
		}
		
		return action;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getActionParameters(JSONObject dict) {
		Map<String, String> _parameters = new HashMap<String, String>();		
		JSONArray jsonParameters = Action.GetActionParameters(dict);
		if(jsonParameters == null) return null;
		
		for (int j = 0; j < jsonParameters.size(); j++) {
			JSONObject dict2 = (JSONObject) jsonParameters.get(j);			
			Set<Entry<Object, Object>> set = dict2.entrySet();

		    for(Entry<Object, Object> entry : set) {
		    	String value = parameters.get(entry.getKey().toString());
		    	if(entry.getValue().toString().isEmpty()) {
		    	
			    	if(value == null || value.isEmpty()) {
			    		TestRunner.loggers.appendInfo(String.format(TestSetupStringRepository.ERRORLOGS_INVALID_TEMPLATE_PARAMETERS,
			    				templateName,
			    				entry.getKey().toString()));
			    		TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_INVALID_TEMPLATE_PARAMETERS,
			    				templateName,
			    				entry.getKey().toString()));
			    		TestCaseRunner.validationMessage = "parameters in template and caller don't match";
			    		return null;
			    	}
		    	} else {
		    		value = entry.getValue().toString();
		    	}
		    	
		    	_parameters.put(entry.getKey().toString(), value);
		    }	    
		}
			
		return _parameters;
	}
	
	private void runTemplate() {
		Collections.sort(actionList);
		
		for(Action action : actionList) {
			ActionRunner ar = new ActionRunner(action);
			Boolean _continue = true;
			_continue = ar.execute();
			if(_continue == null || !_continue) {
				break;
			}
		}
	}
	
}
