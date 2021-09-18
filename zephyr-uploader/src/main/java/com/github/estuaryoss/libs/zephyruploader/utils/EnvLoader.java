package com.github.estuaryoss.libs.zephyruploader.utils;

import com.github.estuaryoss.libs.zephyruploader.component.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.constants.ZephyrParams;
import com.github.estuaryoss.libs.zephyruploader.env.Environment;


public class EnvLoader {
    private static final Environment env = new Environment();

    public static ZephyrConfig getZephyrConfigFromEnv() {
        ZephyrConfig zephyrConfig = new ZephyrConfig();

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.USERNAME.getZephyrParam()) != null) {
            zephyrConfig.setUsername(env.getEnvAndVirtualEnv().get(ZephyrParams.USERNAME.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.PASSWORD.getZephyrParam()) != null) {
            zephyrConfig.setPassword(env.getEnvAndVirtualEnv().get(ZephyrParams.PASSWORD.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.JIRA_URL.getZephyrParam()) != null) {
            zephyrConfig.setJiraUrl(env.getEnvAndVirtualEnv().get(ZephyrParams.JIRA_URL.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.PROJECT_KEY.getZephyrParam()) != null) {
            zephyrConfig.setProjectKey(env.getEnvAndVirtualEnv().get(ZephyrParams.PROJECT_KEY.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.RELEASE_VERSION.getZephyrParam()) != null) {
            zephyrConfig.setReleaseVersion(env.getEnvAndVirtualEnv().get(ZephyrParams.RELEASE_VERSION.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.TEST_CYCLE.getZephyrParam()) != null) {
            zephyrConfig.setTestCycle(env.getEnvAndVirtualEnv().get(ZephyrParams.TEST_CYCLE.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.REPORT_PATH.getZephyrParam()) != null) {
            zephyrConfig.setReportPath(env.getEnvAndVirtualEnv().get(ZephyrParams.REPORT_PATH.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.FOLDER_NAME.getZephyrParam()) != null) {
            zephyrConfig.setFolderName(env.getEnvAndVirtualEnv().get(ZephyrParams.FOLDER_NAME.getZephyrParam()));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.NO_OF_THREADS.getZephyrParam()) != null) {
            zephyrConfig.setNoOfThreads(Integer.parseInt(env.getEnvAndVirtualEnv().get(ZephyrParams.NO_OF_THREADS.getZephyrParam())));
        }

        if (env.getEnvAndVirtualEnv().get(ZephyrParams.RECREATE_FOLDER.getZephyrParam()) != null) {
            zephyrConfig.setRecreateFolder(Boolean.parseBoolean(env.getEnvAndVirtualEnv().get(ZephyrParams.RECREATE_FOLDER.getZephyrParam())));
        }

        return zephyrConfig;
    }

}
