package com.github.estuaryoss.libs.zephyruploader.component;

import com.github.estuaryoss.libs.zephyruploader.model.TestExecutionStatus;
import com.github.estuaryoss.libs.zephyruploader.model.TestStatus;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrMetaInfo;
import com.github.estuaryoss.libs.zephyruploader.service.ZephyrService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class ZephyrUploader {
    ZephyrService zephyrService;
    Map<String, List<String>> testData;

    @Autowired
    public ZephyrUploader(ZephyrService zephyrService) {
        this.zephyrService = zephyrService;
    }


    /**
     * Data input from a list of objects
     *
     * @param testResults
     * @return A list of executions
     * @throws InterruptedException
     */
    public List<String> updateJiraZephyr(List<LinkedHashMap<String, String>> testResults) throws InterruptedException {
        testData = getMapForExecutionDetails(testResults);

        return uploadResultsToJira();
    }


    /**
     * Data input from Excel file as String[][]
     *
     * @param testResults
     * @throws InterruptedException
     */
    public List<String> updateJiraZephyr(String[][] testResults) throws InterruptedException {
        testData = getMapForExecutionDetails(testResults);

        return uploadResultsToJira();
    }

    private List<String> uploadResultsToJira() throws InterruptedException {
        List<String> executionIds = new ArrayList<>();

        int poolSize = this.zephyrService.getZephyrConfig().getNoOfThreads();
        boolean recreateFolder = this.zephyrService.getZephyrConfig().isRecreateFolder();

        String folderName = this.zephyrService.getZephyrConfig().getFolderName();

        String folderNameWithDatestamp = String.format("%s_%s", folderName, LocalDate.now());

        String projectId = zephyrService.getProjectByKey(this.zephyrService.getZephyrConfig().getProjectKey());
        String versionId = zephyrService.getVersionForProjectId(this.zephyrService.getZephyrConfig().getReleaseVersion(), projectId);
        String cycleId = zephyrService.getCycleId(this.zephyrService.getZephyrConfig().getTestCycle(), projectId, versionId);
        Integer folderId = zephyrService.getFolderForCycleId(folderNameWithDatestamp, cycleId, projectId, versionId);

        if (recreateFolder && folderId != 0) {
            log.info(String.format("Recreating folder = %s ", folderNameWithDatestamp));
            zephyrService.deleteFolderFromCycle(folderId, projectId, versionId, cycleId);
            TimeUnit.SECONDS.sleep(3);
            folderId = zephyrService.createFolderForCycle(projectId, versionId, cycleId, folderNameWithDatestamp);
        }

        if (folderId == 0) {
            log.info(String.format("Creating folder = %s", folderNameWithDatestamp));
            folderId = zephyrService.createFolderForCycle(projectId, versionId, cycleId, folderNameWithDatestamp);
        }

        ZephyrMetaInfo zephyrMetaInfo = ZephyrMetaInfo.builder()
                .folderId(folderId)
                .cycleId(cycleId)
                .projectId(projectId)
                .versionId(versionId)
                .build();

        BlockingQueue jobQueue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, jobQueue);

        List<Callable> zephyrExecutions = getZephyrExecutionsList(zephyrMetaInfo, this.zephyrService.getZephyrConfig());
        List<Future<String>> zephyrExecutionsList = new ArrayList<>();

        log.info("Executing with a maximum thread pool of: " + poolSize);

        zephyrExecutions.forEach(zephyrExecution -> {
            Future<String> execution = executor.submit(zephyrExecution);
            zephyrExecutionsList.add(execution);
        });

        zephyrExecutionsList.forEach(execution -> {
            try {
                String executionId = execution.get();
                if (executionId != null) executionIds.add(executionId);
            } catch (ExecutionException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });

        executor.shutdown();

        return executionIds;
    }

    private Map<String, List<String>> getMapForExecutionDetails(String[][] excelData) {
        Map<String, List<String>> mapWithExecutionDetails = new LinkedHashMap<>();
        Arrays.stream(excelData).forEach(row -> mapWithExecutionDetails.put(row[0], Arrays.asList(row)));

        return mapWithExecutionDetails;
    }

    private Map<String, List<String>> getMapForExecutionDetails(List<LinkedHashMap<String, String>> testResults) {
        Map<String, List<String>> mapWithExecutionDetails = new LinkedHashMap<>();
        testResults.forEach(elem -> {
            List<String> row = new ArrayList<>(elem.values());
            mapWithExecutionDetails.put(row.get(0), row);
        });

        return mapWithExecutionDetails;
    }

    private List<Callable> getZephyrExecutionsList(ZephyrMetaInfo zephyrMetaInfo, ZephyrConfig zephyrConfig) {
        List<Callable> threadList = new ArrayList<>();
        for (String key : testData.keySet()) {
            Callable callable = () -> {
                String executionId = null;
                try {
                    executionId = createAndUpdateZephyrExecution(zephyrMetaInfo, zephyrConfig, key);
                } catch (Exception e) {
                    log.error(String.format("Failed to create/update test execution for key=%s\n", key) +
                            ExceptionUtils.getStackTrace(e));
                }

                return executionId;
            };
            threadList.add(callable);
            log.info(String.format("### Thread %s added to the list ###", key));
        }
        return threadList;
    }

    private String createAndUpdateZephyrExecution(ZephyrMetaInfo zephyrDetails, ZephyrConfig zephyrCfg, String key) {
        log.info(String.format("Getting issue by key=%s", key));
        String issueId = zephyrService.getIssueByKey(key);
        log.info(String.format("Got issue id=%s", issueId));

        String executionId = zephyrService.createNewExecution(issueId, zephyrDetails, zephyrCfg);
        log.info(String.format("Created new execution id=%s", executionId));

        TestExecutionStatus testExecutionStatus = TestExecutionStatus.SKIPPED;

        try {
            testExecutionStatus = TestExecutionStatus.valueOf(testData.get(key).get(zephyrCfg.getExecutionStatusColumn()));
        } catch (IllegalArgumentException e) {
            executionId = zephyrService.updateExecutionId(executionId,
                    TestStatus.NOT_EXECUTED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));

            return executionId;
        }

        switch (testExecutionStatus) {
            case SUCCESS:
                executionId = zephyrService.updateExecutionId(executionId,
                        TestStatus.PASSED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));
                break;
            case FAILURE:
                executionId = zephyrService.updateExecutionId(executionId,
                        TestStatus.FAILED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));
                break;
            case SKIPPED:
                executionId = zephyrService.updateExecutionId(executionId,
                        TestStatus.NOT_EXECUTED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));
                break;
            default:
                log.error(String.format("Invalid execution status %s", testExecutionStatus.getStatus()));
        }

        return executionId;
    }

}


