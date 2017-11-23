package com.comodo.qa.automation.resources;

public class LoggerStringRepository {
	public static final String LOG4J_PROPERTIES = "log4j.properties";
	
	//generic
	public static final String CONSOLE_N0_ERRORS = "All executions finished without errors.";
	public static final String CONSOLE_ERRORS = "Execution encountered %s error(s)! Check error.log for more details!";
	
	//info logger
	public static final String INFOLOGS_STARTED = "Info Logger Started.";
	public static final String INFOLOGS_INIT_ERROR = "Info logger not initialized.";
	
	
	//error logger
	public static final String ERRORLOGS_STARTED = "Error Logger Started.";
	public static final String ERRORLOGS_INIT_ERROR = "Error logger not initialized.";

}
