package com.github.estuaryoss.libs.zephyruploader.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
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
