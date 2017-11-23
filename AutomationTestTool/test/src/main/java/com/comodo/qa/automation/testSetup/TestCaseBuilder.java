package com.comodo.qa.automation.testSetup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comodo.qa.automation.actions.*;
import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testRunner.TestRunner;
import com.comodo.qa.browsers.tools.FileHelper;

import org.json.simple.JSONArray;

public class TestCaseBuilder {
	private TestCase testCase = null;
	private Action action = null;
	private List<Action> actionList = null;
	private File testSetupFile;
	
	public static JSONParser jsonParser = null;
	public static JSONObject jsonObject = null;
	public static JSONArray jsonActionList = null;
	
	public TestCaseBuilder() {
		testCase = new TestCase();
	}
	
	public TestCase getTestCase() {
		return testCase;
	}
	
	public void setExecution(Execution execution) {
		testCase.setExecution(execution);
	}
	
	public void setId(String id) {
		testCase.setId(id);
	}
	
	public void setTitle(String title) {
		testCase.setTitle(title);
	}
	
	public void setCaseId(String caseId) {
		testCase.setCaseId(caseId);
	}
	
	public void setStatus(int status) {
		testCase.setStatus(status);
	}
	
	public void manageCreateActionList() {
		initActionList();
		
		setTestSetupFile(this.testCase.getCaseId());
		FileReader reader = getFileReader();
		if(reader == null) return;
		
		setJsonObject(reader);
		if(jsonObject == null) return;
		
		manageTestSetupMapping();
	}
	
	private void initActionList() {
		if (this.testCase == null) return;
		
		actionList = new ArrayList<Action>();
	}
	
	private void setTestSetupFile(String fileName) {
		String rootPath = String.format(TestSetupStringRepository.PATTERN_IMPLEMENTATION_ROOT_PATH, 
				TestSetup.resourceFolder.getPath(), 
				TestSetupStringRepository.IMPLEMENTATIONS_FOLDER_NAME);
		
		String path = String.format("%s.json", fileName);
		testSetupFile = FileHelper.getFileFromRootFolder(rootPath, path);
	}
	
	private FileReader getFileReader() {
		FileReader reader = null;
		
		if(testSetupFile == null) {
			reader = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_IMPLEMENTATION_FILE_READ_MESSAGE, 
							this.testCase.getCaseId()));
			return reader;
		}

		try {
			reader = new FileReader(testSetupFile);
			
			TestRunner.loggers.appendDebug(
					String.format(TestSetupStringRepository.INFOLOGS_CASES_FILE_MESSAGE, 
							testSetupFile));
		} catch (FileNotFoundException e) {
			reader = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_IMPLEMENTATION_FILE_READ_MESSAGE, 
							this.testCase.getCaseId()));
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
							testSetupFile.getPath()));
		} catch (ParseException e) {
			jsonObject = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_PARSE_MESSAGE, 
							testSetupFile.getPath()));
		}
	}
	
	private void manageTestSetupMapping() {
		setActionList();
		if(jsonActionList == null) return;
		
		for (int i = 0; i < jsonActionList.size(); i++) {	
			Action action = getAction(i);
			if(action == null) {
				return;
			}
			actionList.add(action);
		}
		
		testCase.setActionList(actionList);
		
		TestRunner.loggers.appendDebug(
				String.format(TestSetupStringRepository.INFOLOGS_CASE_IMPORTED_MESSAGE, 
						testCase.getCaseId()));
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
		
		action.setParameters(getActionParameters(dict));
			
		return action;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getActionParameters(JSONObject dict) {
		Map<String, String> parameters = new HashMap<String, String>();
		JSONArray jsonParameters = Action.GetActionParameters(dict);
		if(jsonParameters == null) return null;
		
		for (int j = 0; j < jsonParameters.size(); j++) {
			JSONObject dict2 = (JSONObject) jsonParameters.get(j);			
			Set<Entry<Object, Object>> set = dict2.entrySet();
	
			for(Entry<Object, Object> entry : set) {
				parameters.put(entry.getKey().toString(), entry.getValue().toString());
			}	    
		}
			
		return parameters;
	}
	
}
