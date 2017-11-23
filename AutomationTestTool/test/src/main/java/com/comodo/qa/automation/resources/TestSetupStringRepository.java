package com.comodo.qa.automation.resources;

public class TestSetupStringRepository {
	public static final String RESOURCES_FOLDER_NAME = "Resources";
	public static final String SETTINGS_FOLDER_NAME = "Settings";
	public static final String SETTINGS_FILE_NAME = "Settings";
	public static final String IMPLEMENTATIONS_FOLDER_NAME = "Implementations";
	public static final String PATTERN_SETTINGS_PATH = "%s\\%s\\%s.json";
	public static final String PATTERN_IMPLEMENTATION_ROOT_PATH = "%s\\%s";
	public static final String PATTERN_IMAGE_PATH = "%s\\%s\\%s.png";
	public static final String IMAGES_FOLDER_NAME = "Images";
	
	public static final String ERRORLOGS_INVALID_ROOT_FOLDER = "Invalid container folder path.";
	public static final String ERRORLOGS_FILE_READ_MESSAGE = "Failed to open or read the file: %s.";
	public static final String ERRORLOGS_IMPLEMENTATION_FILE_READ_MESSAGE = "Failed to open or read the file: %s. Test case is not implemented.";
	public static final String ERRORLOGS_IMPORT_ERROR = "Failed to import execution from [%s] because %s%s.";
	public static final String ERRORLOGS_PARSE_MESSAGE = "Failed to parse Json in file: %s.";
	public static final String ERRORLOGS_INVALID_TEMPLATE_NAME = "Missing or invalid template name.";
	public static final String ERRORLOGS_INVALID_TEMPLATE_PARAMETERS = "Missing or invalid parameter for template %s : [%s]";
	public static final String ERRORLOGS_MISSING_PARAMETERS = "Missing or invalid parameter list for action [%s].";
	public static final String ERRORLOGS_MISSING_PARAMETER = "Parameter [%s] is missing or invalid.";
	public static final String ERRORLOGS_ACTION_NOT_IMPLEMENTED = "Action [%s] is not implemented.";
	public static final String ERRORLOGS_ACTION_NOT_INVOKED = "Action [%s] cannot be invoked. Reason : %s";
	
	public static final String INFOLOGS_ROOT_FOLDER_MESSAGE = "Looking for execution import in the folder: %s.";
	public static final String INFOLOGS_SETUP_FILE_MESSAGE = "Execution import file found: %s.";
	public static final String INFOLOGS_SETTINGS_FILE_MESSAGE = "Settings file found: %s.";
	public static final String INFOLOGS_CASES_FILE_MESSAGE = "Case file found: %s.";
	public static final String INFOLOGS_CASE_IMPORTED_MESSAGE = "Case [%s] imported.";
	public static final String INFOLOGS_TEMPLATES_FILE_MESSAGE = "Template file found: %s.";
	public static final String INFOLOGS_TEMPLATE_IMPORTED_MESSAGE = "Template [%s] imported.";
	public static final String INFOLOGS_SETUP_FILECOUNT_MESSAGE = "Setup file(s) found: %d";
	public static final String INFOLOGS_SUCCESSFUL_IMPORT_MESSAGE = "Execution imported from %s successful%s%24s%s test(s) to be run, %s not implemented, %s to be skipped, on Windows%s %s.";
	public static final String INFOLOGS_START_EXECUTING_TEST_CASE = "Started running the test case : %s (%s)";
	public static final String INFOLOGS_START_RERUNING_TEST_CASE = "Started re-running the test case : %s (%s)";
	public static final String INFOLOGS_STOPPED_EXECUTING_TEST_CASE = "Stopped running the test case : %s, because %s";
	public static final String INFOLOGS_END_EXECUTING_TEST_CASE = "Finished running the test case: %s, status: %s";
	public static final String INFOLOGS_END_RERUNING_TEST_CASE = "Finished re-running the test case: %s, status: %s";
	public static final String INFOLOGS_FAILED_TO_REPORT_TO_TESTRAIL = "Failed to report to TestRail because: %s";
	public static final String INFOLOGS_FAILED_TO_IMPORT_FROM_TESTRAIL = "Failed to import from TestRail because: %s";
	
	public static final String VALIDATION_WRONG_ENVIRONMENT = "Wrong environment to run this execution: [Windows%s %s]";
	
}
