package com.github.estuaryoss.libs.zephyruploader;

import com.github.estuaryoss.libs.zephyruploader.model.TestExecutionStatus;
import com.github.estuaryoss.libs.zephyruploader.model.TestStatus;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrMetaInfo;
import com.github.estuaryoss.libs.zephyruploader.service.ZephyrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class ZephyrUploader {
    private static final Logger log = LoggerFactory.getLogger(ZephyrUploader.class);
    ZephyrService zephyrService;
    Map<String, List<String>> testData;

    public ZephyrUploader(ZephyrService zephyrService) {
        this.zephyrService = zephyrService;
    }


    /**
     * Data input from a list of objects
     *
     * @param testResults
     * @throws InterruptedException
     */
    public void updateJiraZephyr(List<LinkedHashMap<String, String>> testResults) throws InterruptedException {
        testData = getMapForExecutionDetails(testResults);

        uploadResultsToJira();
    }


    /**
     * Data input from Excel file as String[][]
     *
     * @param testResults
     * @throws InterruptedException
     */
    public void updateJiraZephyr(String[][] testResults) throws InterruptedException {
        testData = getMapForExecutionDetails(testResults);

        uploadResultsToJira();
    }

    private void uploadResultsToJira() throws InterruptedException {
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

        log.info("Executing with a maximum thread pool of: " + poolSize);

        zephyrExecutions.forEach(zephyrExecution -> {
            executor.submit(zephyrExecution);
        });

        while (executor.getActiveCount() > 0 && jobQueue.size() > 0) {
            //wait to complete
            Thread.sleep(1000);
        }

        //wait for others to finish also
        Thread.sleep(5000);
        executor.shutdown();
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
                try {
                    createAndUpdateZephyrExecution(zephyrMetaInfo, zephyrConfig, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            };
            threadList.add(callable);
            log.info(String.format("### Thread %s added to the list ###", key));
        }
        return threadList;
    }

    private void createAndUpdateZephyrExecution(ZephyrMetaInfo zephyrDetails, ZephyrConfig zephyrCfg, String key) {
        log.info(String.format("getting issue by key=%s", key));
        String issueId = zephyrService.getIssueByKey(key);
        log.info(String.format("got issue id=%s", issueId));

        String executionId = zephyrService.createNewExecution(issueId, zephyrDetails, zephyrCfg);
        log.info(String.format("created new executionId=%s", executionId));

        if (testData.get(key).get(zephyrCfg.getExecutionStatusColumn()).equals(TestExecutionStatus.SUCCESS.getStatus())) {
            zephyrService.updateExecutionId(executionId,
                    TestStatus.PASSED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));
        } else if (testData.get(key).get(zephyrCfg.getExecutionStatusColumn()).equals(TestExecutionStatus.FAILURE.getStatus())) {
            zephyrService.updateExecutionId(executionId,
                    TestStatus.FAILED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));
        } else {
            zephyrService.updateExecutionId(executionId,
                    TestStatus.NOT_EXECUTED.getId(), testData.get(key).get(zephyrCfg.getCommentsColumn()));
        }
    }

}


