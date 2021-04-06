package com.github.estuaryoss.libs.zephyruploader.model;

public class ZephyrConfig {
    private String username;
    private String password;
    private String jiraUrl;
    private String projectKey;
    private String releaseVersion;
    private String testCycle;
    private String reportPath;
    private String folderName = "Results";
    private int noOfThreads = 1;
    private int executionStatusColumn = 6;
    private int commentsColumn = 8;
    private boolean recreateFolder = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getTestCycle() {
        return testCycle;
    }

    public void setTestCycle(String testCycle) {
        this.testCycle = testCycle;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public int getNoOfThreads() {
        return noOfThreads;
    }

    public void setNoOfThreads(int noOfThreads) {
        this.noOfThreads = noOfThreads;
    }

    public boolean isRecreateFolder() {
        return recreateFolder;
    }

    public void setRecreateFolder(boolean recreateFolder) {
        this.recreateFolder = recreateFolder;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getExecutionStatusColumn() {
        return executionStatusColumn;
    }

    public void setExecutionStatusColumn(int executionStatusColumn) {
        this.executionStatusColumn = executionStatusColumn;
    }

    public int getCommentsColumn() {
        return commentsColumn;
    }

    public void setCommentsColumn(int commentsColumn) {
        this.commentsColumn = commentsColumn;
    }
}
