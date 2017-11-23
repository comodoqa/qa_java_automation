package com.comodo.qa.automation.testSetup;

public class Settings {
	public boolean isTestRailImportEnabled;
	public boolean isTestRailReportingEnabled;
	public boolean isDebuggingEnabled;
	public String version;
	public String runId;
	public String testRaillUrl;
	public String testRailPassword;
	public String testRailUsername;
	public String browser;
	public String productId;
	public int rerunIfFailed;
	public float multiplier;
	
	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public String getProductId() {
		return productId;
	}

	public int getRerunIfFailed() {
		return rerunIfFailed;
	}

	public void setRerunIfFailed(int rerunIfFailed) {
		this.rerunIfFailed = rerunIfFailed;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	private String channelId;
	
	public boolean isTestRailImportEnabled() {
		return isTestRailImportEnabled;
	}
	
	public void setTestRailImportEnabled(boolean isTestRailImportEnabled) {
		this.isTestRailImportEnabled = isTestRailImportEnabled;
	}
	
	public boolean isTestRailReportingEnabled() {
		return isTestRailReportingEnabled;
	}
	
	public void setTestRailReportingEnabled(boolean isTestRailReportingEnabled) {
		this.isTestRailReportingEnabled = isTestRailReportingEnabled;
	}
	
	public boolean isDebuggingEnabled() {
		return isDebuggingEnabled;
	}
	
	public void setDebuggingEnabled(boolean isDebuggingEnabled) {
		this.isDebuggingEnabled = isDebuggingEnabled;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getRunId() {
		return runId;
	}
	
	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getTestRaillUrl() {
		return testRaillUrl;
	}

	public void setTestRaillUrl(String testRaillUrl) {
		this.testRaillUrl = testRaillUrl;
	}

	public String getTestRailPassword() {
		return testRailPassword;
	}

	public void setTestRailPassword(String testRailPassword) {
		this.testRailPassword = testRailPassword;
	}

	public String getTestRailUsername() {
		return testRailUsername;
	}

	public void setTestRailUsername(String testRailUsername) {
		this.testRailUsername = testRailUsername;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
	
}
