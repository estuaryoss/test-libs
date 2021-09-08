### Description

Upload test results in Jira Zephyr library used to support standardized testing.

### Call example

```bash
java -cp zephyr-uploader.jar "com.github.estuaryoss.libs.zephyruploader.Main" -username auto-robot -password mySecretPasswd123! \
-jiraUrl http://jira.yourcompany.com/rest/ -projectKey AIP -releaseVersion 1.2-UP2020 -testCycle Regression -reportPath Regression_FTP.xls \
-noOfThreads=10 -folderName Results -recreateFolder false 
```

## Artifact maven central

```xml

<dependency>
    <groupId>com.github.estuaryoss.libs</groupId>
    <artifactId>zephyr-uploader</artifactId>
    <version>1.3</version>
</dependency>

<dependency>
<groupId>com.github.estuaryoss.libs</groupId>
<artifactId>zephyr-uploader</artifactId>
<version>1.4-SNAPSHOT</version>
</dependency>
```

## Programmatic example

### Upload from excel report found on disk

```java
Environment env=new Environment();
        ZephyrConfig zephyrConfig=ZephyrConfig.builder()
        .username(env.getEnvAndVirtualEnv().get(ZephyrParams.USERNAME.getZephyrParam()))
        .password(env.getEnvAndVirtualEnv().get(ZephyrParams.PASSWORD.getZephyrParam()))
        .jiraUrl(env.getEnvAndVirtualEnv().get(ZephyrParams.JIRA_URL.getZephyrParam()))
        .projectKey(env.getEnvAndVirtualEnv().get(ZephyrParams.PROJECT_KEY.getZephyrParam()))
        .releaseVersion(env.getEnvAndVirtualEnv().get(ZephyrParams.RELEASE_VERSION.getZephyrParam()))
        .testCycle(env.getEnvAndVirtualEnv().get(ZephyrParams.TEST_CYCLE.getZephyrParam()))
        .reportPath(env.getEnvAndVirtualEnv().get(ZephyrParams.REPORT_PATH.getZephyrParam()))
        .noOfThreads(Integer.parseInt(env.getEnvAndVirtualEnv().get(ZephyrParams.NO_OF_THREADS.getZephyrParam())))
        .recreateFolder(Boolean.parseBoolean(env.getEnvAndVirtualEnv().get(ZephyrParams.RECREATE_FOLDER.getZephyrParam())));
        .build();

        ZephyrUploader zephyrUploader=new ZephyrUploader(new ZephyrService(zephyrConfig));
        String[][]rawExcelData=ExcelReader.readExcel(zephyrConfig.getReportPath());
        zephyrUploader.updateJiraZephyr(rawExcelData);
```

### Upload from json object (Array of objects)

```java
Environment env=new Environment();
        ZephyrConfig zephyrConfig=ZephyrConfig.builder()
        .username(env.getEnvAndVirtualEnv().get(ZephyrParams.USERNAME.getZephyrParam()))
        .password(env.getEnvAndVirtualEnv().get(ZephyrParams.PASSWORD.getZephyrParam()))
        .jiraUrl(env.getEnvAndVirtualEnv().get(ZephyrParams.JIRA_URL.getZephyrParam()))
        .projectKey(env.getEnvAndVirtualEnv().get(ZephyrParams.PROJECT_KEY.getZephyrParam()))
        .releaseVersion(env.getEnvAndVirtualEnv().get(ZephyrParams.RELEASE_VERSION.getZephyrParam()))
        .testCycle(env.getEnvAndVirtualEnv().get(ZephyrParams.TEST_CYCLE.getZephyrParam()))
        .reportPath(env.getEnvAndVirtualEnv().get(ZephyrParams.REPORT_PATH.getZephyrParam()))
        .noOfThreads(Integer.parseInt(env.getEnvAndVirtualEnv().get(ZephyrParams.NO_OF_THREADS.getZephyrParam())))
        .recreateFolder(Boolean.parseBoolean(env.getEnvAndVirtualEnv().get(ZephyrParams.RECREATE_FOLDER.getZephyrParam())));
        .build();

        ZephyrUploader zephyrUploader=new ZephyrUploader(new ZephyrService(zephyrConfig));
        List<LinkedHashMap<String, String>> testResults=...; //read from disk and deserialize with Jackson, or get from Rest API
        zephyrUploader.updateJiraZephyr(testResults);
```

## ! Keep in mind

- You must have a column with the status of each test execution, and the values permitted are: SUCCESS / FAILURE. If
  none is present the test execution will be mapped as 'not executed'.
- You must specify the position of the above column from the Excel file. Default is the 6'th column. If you have the
  execution status on a different column please specify the position with the parameter 'executionStatusColumn'.   
  E.g. -executionStatusColumn=6
- You also can specify the comments column. For example the link where the test logs are. The default is 8'th column.   
  E.g. -commentsColumn=8
- Jira Ids are always on the first column in the Excel sheet

## Precedence

The arguments set with 'java -cp' are stronger than the ones from environment (env vars or 'environment.properties'
file).