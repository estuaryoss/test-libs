package com.github.estuaryoss.libs.zephyruploader.utils;

import com.github.estuaryoss.libs.zephyruploader.component.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.constants.ZephyrParams;

import static org.assertj.core.api.Assertions.assertThat;

public class ZephyrConfigValidator {
    public static void validate(ZephyrConfig zephyrConfig) {
        assertThat(zephyrConfig.getJiraUrl())
                .withFailMessage(ZephyrParams.JIRA_URL.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.JIRA_URL.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getUsername())
                .withFailMessage(ZephyrParams.USERNAME.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.USERNAME.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getPassword())
                .withFailMessage(ZephyrParams.PASSWORD.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.PASSWORD.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getProjectKey())
                .withFailMessage(ZephyrParams.PROJECT_KEY.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.PROJECT_KEY.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getReleaseVersion())
                .withFailMessage(ZephyrParams.RELEASE_VERSION.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.RELEASE_VERSION.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getTestCycle())
                .withFailMessage(ZephyrParams.TEST_CYCLE.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.TEST_CYCLE.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getFolderName())
                .withFailMessage(ZephyrParams.FOLDER_NAME.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.FOLDER_NAME.getZephyrParam() + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getReportPath())
                .withFailMessage(ZephyrParams.REPORT_PATH.getZephyrParam() + " arg was not set. Set this argument or set the env var: '" + ZephyrParams.REPORT_PATH.getZephyrParam() + "'")
                .isNotEqualTo(null);
    }
}
