package com.github.estuaryoss.libs.zephyruploader;

import com.github.estuaryoss.libs.zephyruploader.component.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.constants.ZephyrParams;
import com.github.estuaryoss.libs.zephyruploader.utils.EnvLoader;
import org.apache.commons.cli.*;


public class CliParser {
    private ZephyrConfig zephyrConfig = EnvLoader.getZephyrConfigFromEnv();
    private Options options = new Options();
    private CommandLineParser parser = new DefaultParser();

    public ZephyrConfig parseCommand(String[] args) throws ParseException {
        buildOptions();
        CommandLine line = parser.parse(options, args);

        return getZephyrConfigFromCli(line);
    }

    private void buildOptions() {
        options.addOption("u", ZephyrParams.USERNAME.getZephyrParam(), true, "The username in Jira. E.g. auto-robot");
        options.addOption("p", ZephyrParams.PASSWORD.getZephyrParam(), true, "The username's password in Jira. E.g. whateverSecretPwd!");
        options.addOption("ju", ZephyrParams.JIRA_URL.getZephyrParam(), true, "The base url of Jira'. E.g. http://jira.yourcompany.com/rest/");
        options.addOption("pk", ZephyrParams.PROJECT_KEY.getZephyrParam(), true, "The project key. E.g. AIP");
        options.addOption("rv", ZephyrParams.RELEASE_VERSION.getZephyrParam(), true, "The release version. E.g. 1.1-UP2020");
        options.addOption("tc", ZephyrParams.TEST_CYCLE.getZephyrParam(), true, "The test cycle. E.g. Regression_Automated");
        options.addOption("nt", ZephyrParams.NO_OF_THREADS.getZephyrParam(), true, "The number of threads used to upload the report. E.g. 10");
        options.addOption("rp", ZephyrParams.REPORT_PATH.getZephyrParam(), true, "The file path of the Excel document. E.g. results/regression_2020.xlsx");
        options.addOption("fn", ZephyrParams.FOLDER_NAME.getZephyrParam(), true, "The name of the folder which will be created under the test cycle. E.g. results-build-188");
        options.addOption("rf", ZephyrParams.RECREATE_FOLDER.getZephyrParam(), true, "Recreate or not the folder. E.g. true");
        options.addOption("esc", ZephyrParams.EXECUTION_STATUS_COLUMN.getZephyrParam(), true, "The column where you keep the test status (SUCCESS/FAILURE). E.g. 6");
        options.addOption("cc", ZephyrParams.COMMENTS_COLUMN.getZephyrParam(), true, "The column where you keep additional comments " +
                "for your tests, as the link to the test logs. E.g. 11");
    }

    private ZephyrConfig getZephyrConfigFromCli(CommandLine line) {
        if (line.hasOption(ZephyrParams.USERNAME.getZephyrParam())) {
            zephyrConfig.setUsername(line.getOptionValue(ZephyrParams.USERNAME.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.PASSWORD.getZephyrParam())) {
            zephyrConfig.setPassword(line.getOptionValue(ZephyrParams.PASSWORD.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.JIRA_URL.getZephyrParam())) {
            zephyrConfig.setJiraUrl(line.getOptionValue(ZephyrParams.JIRA_URL.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.PROJECT_KEY.getZephyrParam())) {
            zephyrConfig.setProjectKey(line.getOptionValue(ZephyrParams.PROJECT_KEY.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.RELEASE_VERSION.getZephyrParam())) {
            zephyrConfig.setReleaseVersion(line.getOptionValue(ZephyrParams.RELEASE_VERSION.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.TEST_CYCLE.getZephyrParam())) {
            zephyrConfig.setTestCycle(line.getOptionValue(ZephyrParams.TEST_CYCLE.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.REPORT_PATH.getZephyrParam())) {
            zephyrConfig.setReportPath(line.getOptionValue(ZephyrParams.REPORT_PATH.getZephyrParam()));
        }


        if (line.hasOption(ZephyrParams.FOLDER_NAME.getZephyrParam())) {
            zephyrConfig.setFolderName(line.getOptionValue(ZephyrParams.FOLDER_NAME.getZephyrParam()));
        }

        if (line.hasOption(ZephyrParams.NO_OF_THREADS.getZephyrParam())) {
            zephyrConfig.setNoOfThreads(Integer.parseInt(line.getOptionValue(ZephyrParams.NO_OF_THREADS.getZephyrParam())));
        }

        if (line.hasOption(ZephyrParams.RECREATE_FOLDER.getZephyrParam())) {
            zephyrConfig.setRecreateFolder(Boolean.parseBoolean(line.getOptionValue(ZephyrParams.RECREATE_FOLDER.getZephyrParam())));
        }

        if (line.hasOption(ZephyrParams.EXECUTION_STATUS_COLUMN.getZephyrParam())) {
            zephyrConfig.setExecutionStatusColumn(Integer.parseInt(line.getOptionValue(ZephyrParams.EXECUTION_STATUS_COLUMN.getZephyrParam())));
        }

        if (line.hasOption(ZephyrParams.COMMENTS_COLUMN.getZephyrParam())) {
            zephyrConfig.setCommentsColumn(Integer.parseInt(line.getOptionValue(ZephyrParams.COMMENTS_COLUMN.getZephyrParam())));
        }

        return zephyrConfig;
    }

    public Options getOptions() {
        return options;
    }
}
