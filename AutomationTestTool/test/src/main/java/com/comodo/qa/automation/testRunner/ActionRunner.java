package com.comodo.qa.automation.testRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.comodo.qa.automation.actions.Action;
import com.comodo.qa.automation.resources.TestSetupStringRepository;

public class ActionRunner {
	private Action action = null;
	private Boolean _continue = null;
	private Class<?> _action = null;
	private Object instance = null;
	private Method _method = null;
		
	public ActionRunner(Action action) {
		this.action = action;
		_continue = true;
	}
	
	public Boolean execute() {
		if(TestCaseRunner.assertFailMessage != null) return false;
	
		String templateName = action.getParameters().get("templateName");
		if(action.getType().contains("TEMPLATE")) {
				TemplateRunner tr = new TemplateRunner(templateName, action.getParameters());	
				_continue = tr.execute();
		} else {
			_continue = runAction();
		}
		
		return _continue;
	}
	
	private Boolean runAction() {
		setClass();
		setObjectInstance();
		setMethod();
		Boolean result = invokeMethod();
	
		return result;
	}
	
	private Class<?> setClass() {
		try {
			_action = Class.forName("com.comodo.qa.automation.actions." + action.getType());
		} catch (ClassNotFoundException e) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_IMPLEMENTED,
					action.getType()));
			TestCaseRunner.assertFailMessage = String.format("Action [%s] not implemented", action.getType());
			_action = null;
		}
		
		return _action;
	} 
	
	private Object setObjectInstance() {
		if(_action == null) return null;
		
		try {
			instance = _action.newInstance();
		} catch (InstantiationException e1) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e1.toString()));
		} catch (IllegalAccessException e1) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e1.toString()));
		}
		
		return instance;
	}
	
	private Method setMethod() {
		if(_action == null) return null;
		
		try {
			_method = _action.getMethod("run", Map.class);
		} catch (NoSuchMethodException e) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
		} catch (SecurityException e) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
		}
		
		return _method;
	}
	
	private Boolean invokeMethod() {
		if(_method == null) return false;
		
		Boolean result = null;
		
		try {
			result = (Boolean)_method.invoke(instance, action.getParameters());
		} catch (IllegalAccessException e) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
			Action.SetAssertionMessage(true, String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
		} catch (IllegalArgumentException e) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
			Action.SetAssertionMessage(true, String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
		} catch (InvocationTargetException e) {
			TestRunner.loggers.appendError(String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
			Action.SetAssertionMessage(true, String.format(TestSetupStringRepository.ERRORLOGS_ACTION_NOT_INVOKED,
					action.getType(), 
					e.toString()));
			e.printStackTrace();
		}
		
		return result;
	}
	
}
