package com.github.estuaryoss.libs.zephyruploader;

import com.github.estuaryoss.libs.zephyruploader.constants.CliConstants;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.service.ZephyrService;
import com.github.estuaryoss.libs.zephyruploader.utils.ExcelReader;
import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class Main {
    public static final int ERROR = 1;
    public static final int SUCCESS = 0;

    public static void main(String[] args) throws Exception {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);

        CliParser cliParser = new CliParser();
        ZephyrConfig zephyrConfig = cliParser.parseCommand(args);

        if (!isCliArgFilled(zephyrConfig)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    CliConstants.ARTIFACT_NAME + ".jar Main -" +
                    CliConstants.USERNAME + String.format(" <%s> -", CliConstants.USERNAME) +
                    CliConstants.PASSWORD + String.format(" <%s> -", CliConstants.PASSWORD) +
                    CliConstants.JIRA_URL + String.format(" <%s> -", CliConstants.JIRA_URL) +
                    CliConstants.PROJECT_KEY + String.format(" <%s> -", CliConstants.PROJECT_KEY) +
                    CliConstants.RELEASE_VERSION + String.format(" <%s> -", CliConstants.RELEASE_VERSION) +
                    CliConstants.TEST_CYCLE + String.format(" <%s> -", CliConstants.TEST_CYCLE) +
                    CliConstants.NO_OF_THREADS + String.format(" <%s> -", CliConstants.NO_OF_THREADS) +
                    CliConstants.RECREATE_FOLDER + String.format(" <%s> -", CliConstants.RECREATE_FOLDER) +
                    CliConstants.REPORT_PATH + String.format(" <%s> -", CliConstants.REPORT_PATH) +
                    CliConstants.EXECUTION_STATUS_COLUMN + String.format(" <%s> -", CliConstants.EXECUTION_STATUS_COLUMN) +
                    CliConstants.COMMENTS_COLUMN + String.format(" <%s>", CliConstants.COMMENTS_COLUMN), cliParser.getOptions());
            assertZephyrConfigIsSet(zephyrConfig);

            System.exit(ERROR);
        }

        assertZephyrConfigIsSet(zephyrConfig);

        ZephyrUploader zephyrUploader = new ZephyrUploader(new ZephyrService(zephyrConfig));
        String[][] rawExcelData = ExcelReader.readExcel(zephyrConfig.getReportPath());
        zephyrUploader.updateJiraZephyr(rawExcelData);

        System.exit(SUCCESS);
    }

    private static boolean isCliArgFilled(ZephyrConfig config) {
        if (config.getJiraUrl() == null || config.getUsername() == null || config.getPassword() == null ||
                config.getProjectKey() == null || config.getReleaseVersion() == null || config.getTestCycle() == null ||
                config.getTestCycle() == null) {
            return false;
        }

        return true;
    }

    private static void assertZephyrConfigIsSet(ZephyrConfig zephyrConfig) {
        assertThat(zephyrConfig.getUsername())
                .withFailMessage(CliConstants.USERNAME + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.USERNAME + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getUsername())
                .withFailMessage(CliConstants.PASSWORD + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.PASSWORD + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getJiraUrl())
                .withFailMessage(CliConstants.JIRA_URL + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.JIRA_URL + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getJiraUrl())
                .withFailMessage(CliConstants.PROJECT_KEY + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.PROJECT_KEY + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getJiraUrl())
                .withFailMessage(CliConstants.RELEASE_VERSION + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.RELEASE_VERSION + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getJiraUrl())
                .withFailMessage(CliConstants.TEST_CYCLE + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.TEST_CYCLE + "'")
                .isNotEqualTo(null);
        assertThat(zephyrConfig.getJiraUrl())
                .withFailMessage(CliConstants.REPORT_PATH + " arg was not set through CLI. Set this argument or set the env var: '" + CliConstants.REPORT_PATH + "'")
                .isNotEqualTo(null);
    }
}
