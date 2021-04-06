package com.github.estuaryoss.libs.zephyruploader.utils;

import com.github.estuaryoss.libs.zephyruploader.constants.CliConstants;
import com.github.estuaryoss.libs.zephyruploader.env.Environment;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrConfig;


public class EnvLoader {
    private static final Environment env = new Environment();

    public static ZephyrConfig getZephyrConfigFromEnv() {
        ZephyrConfig zephyrConfig = new ZephyrConfig();

        if (env.getEnvAndVirtualEnv().get(CliConstants.USERNAME) != null) {
            zephyrConfig.setUsername(env.getEnvAndVirtualEnv().get(CliConstants.USERNAME));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.PASSWORD) != null) {
            zephyrConfig.setPassword(env.getEnvAndVirtualEnv().get(CliConstants.PASSWORD));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.JIRA_URL) != null) {
            zephyrConfig.setJiraUrl(env.getEnvAndVirtualEnv().get(CliConstants.JIRA_URL));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.PROJECT_KEY) != null) {
            zephyrConfig.setProjectKey(env.getEnvAndVirtualEnv().get(CliConstants.PROJECT_KEY));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.RELEASE_VERSION) != null) {
            zephyrConfig.setReleaseVersion(env.getEnvAndVirtualEnv().get(CliConstants.RELEASE_VERSION));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.TEST_CYCLE) != null) {
            zephyrConfig.setTestCycle(env.getEnvAndVirtualEnv().get(CliConstants.TEST_CYCLE));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.REPORT_PATH) != null) {
            zephyrConfig.setReportPath(env.getEnvAndVirtualEnv().get(CliConstants.REPORT_PATH));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.FOLDER_NAME) != null) {
            zephyrConfig.setFolderName(env.getEnvAndVirtualEnv().get(CliConstants.FOLDER_NAME));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.NO_OF_THREADS) != null) {
            zephyrConfig.setNoOfThreads(Integer.parseInt(env.getEnvAndVirtualEnv().get(CliConstants.NO_OF_THREADS)));
        }

        if (env.getEnvAndVirtualEnv().get(CliConstants.RECREATE_FOLDER) != null) {
            zephyrConfig.setRecreateFolder(Boolean.parseBoolean(env.getEnvAndVirtualEnv().get(CliConstants.RECREATE_FOLDER)));
        }

        return zephyrConfig;
    }

}
