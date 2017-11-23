package com.comodo.qa.automation.testSetup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.comodo.qa.automation.resources.TestSetupStringRepository;
import com.comodo.qa.automation.testRunner.TestRunner;


public class TestSetup {
	public List<String> fileList = null;
	public static List<Execution> executionList = null;
	public static File resourceFolder;
	public static Settings settings;
	
	public void RunSetup() {
		if(!TestRunner.loggers.isLoggerValid()) return;		
		
		setRootFolder();
		manageSettings();		
		manageExecutionMapping();
	}
	
	private void manageFileDiscovery() {
		initFileList();
		if (!isValidRootFolder()) return;
		
		setTestSetupFiles();
		logFileCount();
	}
	
	private void initFileList() {
		fileList = new ArrayList<String>();
	}
	
	private void setRootFolder() {
		File jar = new File(TestRunner
				.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath());
		String path = String.format("%s\\%s", 
				jar.getParentFile().getPath(), 
				TestSetupStringRepository.RESOURCES_FOLDER_NAME);

		resourceFolder = new File(path.replace("%20", " "));
		
		TestRunner.loggers.appendDebug(
				String.format(TestSetupStringRepository.INFOLOGS_ROOT_FOLDER_MESSAGE, 
						path));
	}

	public static boolean isValidRootFolder() {
		if(resourceFolder == null || !resourceFolder.exists()) {
			TestRunner.loggers.appendError(TestSetupStringRepository.ERRORLOGS_INVALID_ROOT_FOLDER);
			return false;
		}
		
		return true;
	}

	private void setTestSetupFiles() {
		for(File fileEntry : resourceFolder.listFiles()) {
			if(fileEntry.getName().contains("csv")) {
				fileList.add(fileEntry.getPath());
				TestRunner.loggers.appendDebug(
						String.format(TestSetupStringRepository.INFOLOGS_SETUP_FILE_MESSAGE, 
								fileEntry.getPath()));
			}
		}
	}
	
	private void logFileCount() {
		if(fileList == null) return;
		
		TestRunner.loggers.appendDebug(String.format(TestSetupStringRepository.INFOLOGS_SETUP_FILECOUNT_MESSAGE, 
				fileList.size()));
	}
	
	private void manageSettings() {
		SettingsFactory sf = new SettingsFactory();
		settings = sf.getSettings();	
	}
	
	private void manageExecutionMapping() {
		initExecutionList();
		if(settings.isTestRailImportEnabled()) {
			setExecutionsFromTestRail();
		}
		else {
			manageFileDiscovery();
			if(fileList == null || fileList.isEmpty()) return;
			
			setExecutionsFromFiles();
		}
	}
	
	private void initExecutionList() {
		executionList = new ArrayList<Execution>();
	}
	
	private void setExecutionsFromTestRail() {
		Execution execution = new Execution();
		ExecutionFactory ef = getExecutionFactory();
		if(ef == null) return;
		
		execution = ef.ImportFromTestRail(settings.getRunId());
		if(execution == null) return;
		
		if(!execution.validationResults.isEmpty()) {
			TestRunner.loggers.appendError(
					String.format(TestSetupStringRepository.ERRORLOGS_IMPORT_ERROR,
						"TestRail",
						System.lineSeparator(),
						execution.getPrintableValidationResults()));
		}
		
		executionList.add(execution);
		TestRunner.loggers.appendInfo(
				String.format(TestSetupStringRepository.INFOLOGS_SUCCESSFUL_IMPORT_MESSAGE,
					"TestRail", 
					System.lineSeparator(), "", 
					execution.getTestCases().size(),
					execution.getNotImplemented(),
					execution.getSkipped(), 
					execution.getWindowsVersion(), 
					execution.getWindowsArchitecture()));
	}
	
	private void setExecutionsFromFiles() {
		Execution execution = new Execution();
		ExecutionFactory ef = getExecutionFactory();
		if(ef == null) return;
		
		for(String filePath : fileList){
			try {
				execution = null;
				execution = ef.ImportFromFile(filePath);
			} catch (IOException e) {	
				TestRunner.loggers.appendError( 
						String.format(TestSetupStringRepository.ERRORLOGS_FILE_READ_MESSAGE, 
								filePath));
			}

			if(!execution.validationResults.isEmpty()) {
				TestRunner.loggers.appendError(
						String.format(TestSetupStringRepository.ERRORLOGS_IMPORT_ERROR,
							filePath,
							System.lineSeparator(),
							execution.getPrintableValidationResults()));
			} else {
				executionList.add(execution);
				TestRunner.loggers.appendInfo(
						String.format(TestSetupStringRepository.INFOLOGS_SUCCESSFUL_IMPORT_MESSAGE,
							filePath, 
							System.lineSeparator(), "", 
							execution.getTestCases().size(),
							execution.getNotImplemented(),
							execution.getSkipped(), 
							execution.getWindowsVersion(), 
							execution.getWindowsArchitecture()));
			}
		}
	}
	
	private ExecutionFactory getExecutionFactory() {
		ExecutionFactory ef = new ExecutionFactory();
		if(!ef.validationResults.isEmpty()) {
			TestRunner.loggers.appendError(
				String.format("%s%s", 
					System.lineSeparator(), 
					ef.getPrintableValidationResults()));
			
			return null;
		}

		return ef;
	}
	
}
