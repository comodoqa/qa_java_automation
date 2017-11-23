package com.comodo.qa.automation.testSetup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comodo.qa.automation.actions.Action;

public class TestCase {
	private Execution execution;
	private String id;
	private String title;
	private String caseId;
	private int status;
	private Date timestamp; 
	private List<Action> actionList;
	
	public TestCase() {
		actionList = new ArrayList<Action>();
	}
	
	public Execution getExecution() {
		return execution;
	}
	
	public void setExecution(Execution execution) {
		this.execution = execution;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCaseId() {
		return this.caseId;
	}
	
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public List<Action> getActionList() {
		return this.actionList;
	}
	
	public void setActionList(List<Action> actionList) {
		this.actionList = actionList;
	}
	
}
