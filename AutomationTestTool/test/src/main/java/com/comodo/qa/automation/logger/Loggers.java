package com.comodo.qa.automation.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.comodo.qa.automation.resources.LoggerStringRepository;
import com.comodo.qa.automation.testSetup.TestSetup;

public class Loggers {
	private Logger infoLogger;
	private Logger errorLogger;
	private int errorCount = 0;
	
	public Loggers() {
		initLoggers();
	}

	public boolean isLoggerValid() {
		if(errorLogger == null) {
			System.out.println(LoggerStringRepository.ERRORLOGS_INIT_ERROR);
			return false;
		}
		
		if(infoLogger == null) {
			System.out.println(LoggerStringRepository.INFOLOGS_INIT_ERROR);
			return false;
		}
		
		return true;
	}
	
	public void appendInfo(String message) {
		if(!isLoggerValid()) {
			initLoggers();
			
			return;
		}
		
		infoLogger.info(message);
	}
	
	public void appendDebug(String message) {
		if(!isLoggerValid()) {
			initLoggers();
			
			return;
		}
		
		if(TestSetup.settings == null) return;
		
		if(!TestSetup.settings.isDebuggingEnabled()) return;
		
		infoLogger.info(message);
	}
	
	public void appendError(String message) {
		if(!isLoggerValid()) {
			initLoggers();
			
			return;
		}
		
		errorLogger.error(message);
		errorCount ++;
	}
	
	public int getErrorCount() {
		return errorCount;
	}
	
	private void initLoggers() {
		setDateFormatProperty();
		setLoggerProperty();
		setInfoLogger();
		setErrorLogger();
	}
	
	private void setDateFormatProperty() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("{yyyy-MM-dd}{hh-mm-ss}");
        System.setProperty("current.date", dateFormat.format(new Date()));
	}
	
	private void setLoggerProperty() {
		Loggers.class.getResourceAsStream(LoggerStringRepository.LOG4J_PROPERTIES);
	}
	
	private void setInfoLogger() {
		infoLogger = Logger.getLogger("infoLogger");
		System.out.println(LoggerStringRepository.INFOLOGS_STARTED);
	}
	
	private void setErrorLogger() {
		errorLogger = Logger.getLogger("errorLogger");
		System.out.println(LoggerStringRepository.ERRORLOGS_STARTED);
	}
	
}
