### Description

Upload test results in Jira Zephyr library used to support standardized testing.

### Call example

```bash
java -cp zephyr-uploader.jar "Main" -username auto-robot -password mySecretPasswd123! \
-jiraUrl http://jira.yourcompany.com/rest/ -projectKey AIP -releaseVersion 1.2-UP2020 -testCycle Regression -reportPath Regression_FTP.xls \
-noOfThreads=10 -folderName Results -recreateFolder false 
```

## Artifact maven central

```xml

<dependency>
  <groupId>com.github.estuaryoss.libs</groupId>
  <artifactId>zephyr-uploader</artifactId>
  <version>1.1</version>
</dependency>
```

## Programmatic example

```java
Environment env=new Environment();
        ZephyrConfig zephyrConfig=new ZephyrConfig()
        .setUsername(env.getEnvAndVirtualEnv().get(USERNAME))
        .setPassword(env.getEnvAndVirtualEnv().get(PASSWORD))
        .setJiraUrl(env.getEnvAndVirtualEnv().get(JIRA_URL))
        .setProjectKey(env.getEnvAndVirtualEnv().get(PROJECT_KEY))
        .setReleaseVersion(env.getEnvAndVirtualEnv().get(RELEASE_VERSION))
        .setTestCycle(env.getEnvAndVirtualEnv().get(TEST_CYCLE))
        .setReportPath(env.getEnvAndVirtualEnv().get(REPORT_PATH))
        .setNoOfThreads(Integer.parseInt(env.getEnvAndVirtualEnv().get(NO_OF_THREADS)))
        .setRecreateFolder(Boolean.parseBoolean(env.getEnvAndVirtualEnv().get(RECREATE_FOLDER)));

        ZephyrUploader zephyrUploader=new ZephyrUploader(new ZephyrService(zephyrConfig));
        zephyrUploader.updateJiraZephyr();
```

## ! Keep in mind

- You must have a column with the status of each test execution, and the values permitted are: SUCCESS / FAILURE. If
  none is present the test execution will be mapped as 'not executed'.
- You must specify the position of the above column from the Excel file. Default is the 6'th column. If you have the
  execution status on a different column please specify the position with the parameter 'executionStatusColumn'.   
  E.g. -executionStatusColumn=6
- You also can specify the comments column. For example the link where the test logs are. The default is 8'th column.   
  E.g. -commentsColumn=8

## Precedence

The arguments set with 'java -cp' are stronger than the ones from environment (env vars or 'environment.properties'
file).