package com.comodo.qa.automation.actions;

import java.util.Map;

public interface IAction {
	public Map<String, String> parameters = null;
	public boolean isResultNegated = false;
	public Boolean isPassed = null;
	public String failMessage = null;
	
	public void setAssertionMessage();
}
