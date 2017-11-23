package com.comodo.qa.automation.testSetup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testRunner.TestRunner;

public class SettingsFactory {
	public static JSONParser jsonParser = null;
	public static JSONObject jsonObject = null;
	public static JSONArray testArray = null;
	
	private Settings settings = null;	
	private File settingsFile;
	
	public SettingsFactory() {
		if (!TestSetup.isValidRootFolder()) return;
		
		setSettingsFile();
		FileReader reader = getFileReader();
		if(reader == null) return;

		setJsonObject(reader);
		if(jsonObject == null) return;
		manageSettingsMapping();
		printSettings();
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	private void setSettingsFile() {
		String path = String.format(TestSetupStringRepository.PATTERN_SETTINGS_PATH, 
				TestSetup.resourceFolder.getPath(), 
				TestSetupStringRepository.SETTINGS_FOLDER_NAME,
				TestSetupStringRepository.SETTINGS_FILE_NAME);

		settingsFile = new File(path.replace("%20", " "));
		
		TestRunner.loggers.appendInfo(
				String.format(TestSetupStringRepository.INFOLOGS_SETTINGS_FILE_MESSAGE, 
						settingsFile));
	}
	
	private FileReader getFileReader() {
		FileReader reader = null;

		try {
			reader = new FileReader(settingsFile);
		} catch (FileNotFoundException e) {
			reader = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_FILE_READ_MESSAGE, 
							settingsFile.getPath()));
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
							settingsFile.getPath()));
		} catch (ParseException e) {
			jsonObject = null;
			TestRunner.loggers.appendError( 
					String.format(TestSetupStringRepository.ERRORLOGS_PARSE_MESSAGE, 
							settingsFile.getPath()));
		}
	}
	
	private void manageSettingsMapping() {
		settings = new Settings();
		
		setEnabledFlags();
		setExecutionProperties();
		setTestRailSettings();
		setBrowserSettings();
	}

	private void setEnabledFlags() {
		settings.setTestRailImportEnabled(Boolean
				.parseBoolean((String) jsonObject.get("isTestRailImportEnabled")));
		settings.setTestRailReportingEnabled(Boolean
				.parseBoolean((String) jsonObject.get("isTestRailReportingEnabled")));
		settings.setDebuggingEnabled(Boolean
				.parseBoolean((String) jsonObject.get("isDebuggingEnabled")));
	}
	
	private void setExecutionProperties() {
		settings.setRunId((String) jsonObject.get("runId"));
		settings.setRerunIfFailed(Integer.parseInt((String) jsonObject.get("rerunIfFailed")));
		settings.setMultiplier(Float.parseFloat((String) jsonObject.get("multiplier")));
	}
	
	private void setTestRailSettings() {
		JSONArray testRailSettings = (JSONArray) jsonObject.get("testRail");
		JSONObject dict = (JSONObject) testRailSettings.get(0);
		
		settings.setTestRaillUrl((dict.get("testRaillUrl").toString()));
		settings.setTestRailUsername((dict.get("testRailUsername").toString()));
		settings.setTestRailPassword((dict.get("testRailPassword").toString()));
	}
	
	private void setBrowserSettings() {
		settings.setBrowser((String) jsonObject.get("browser"));
		
		JSONArray testRailSettings = (JSONArray) jsonObject.get(settings.getBrowser());
		JSONObject dict = (JSONObject) testRailSettings.get(0);
		
		settings.setVersion((dict.get("version").toString()));
		settings.setProductId((dict.get("productId").toString()));
		settings.setChannelId((dict.get("channelId").toString()));
	}
	
	private void printSettings() {
		System.out.println("");
		System.out.println("Importing from TestRail is [" + (settings.isTestRailImportEnabled() ? "enabled" : "disabled") + "].");
		System.out.println("Reporting to TestRail is [" + (settings.isTestRailReportingEnabled() ? "enabled" : "disabled") + "].");
		System.out.println("Debugging is [" + (settings.isDebuggingEnabled() ? "enabled" : "disabled") + "].");
		System.out.println("Tested browser: " + settings.getBrowser());
		System.out.println("Tested version: " + settings.getVersion());
		if(settings.isTestRailImportEnabled()) System.out.println("Run Id: " + settings.getRunId());
		System.out.println("Rerun if failed: " + settings.getRerunIfFailed());
		System.out.println("Timer multiplier: " + settings.getMultiplier());
	}
	
}
