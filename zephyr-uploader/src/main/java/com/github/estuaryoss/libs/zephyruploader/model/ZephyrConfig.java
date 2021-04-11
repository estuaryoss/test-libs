package com.github.estuaryoss.libs.zephyruploader.model;

import lombok.Data;

@Data
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
}
