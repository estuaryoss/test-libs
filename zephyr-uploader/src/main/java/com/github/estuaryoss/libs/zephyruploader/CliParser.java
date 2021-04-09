package com.github.estuaryoss.libs.zephyruploader;

import com.github.estuaryoss.libs.zephyruploader.constants.CliConstants;
import com.github.estuaryoss.libs.zephyruploader.model.ZephyrConfig;
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
        options.addOption("u", CliConstants.USERNAME, true, "The username in Jira. E.g. auto-robot");
        options.addOption("p", CliConstants.PASSWORD, true, "The username's password in Jira. E.g. whateverSecretPwd!");
        options.addOption("ju", CliConstants.JIRA_URL, true, "The base url of Jira'. E.g. http://jira.yourcompany.com/rest/");
        options.addOption("pk", CliConstants.PROJECT_KEY, true, "The project key. E.g. AIP");
        options.addOption("rv", CliConstants.RELEASE_VERSION, true, "The release version. E.g. 1.1-UP2020");
        options.addOption("tc", CliConstants.TEST_CYCLE, true, "The test cycle. E.g. Regression_Automated");
        options.addOption("nt", CliConstants.NO_OF_THREADS, true, "The number of threads used to upload the report. E.g. 10");
        options.addOption("rp", CliConstants.REPORT_PATH, true, "The file path of the Excel document. E.g. results/regression_2020.xlsx");
        options.addOption("fn", CliConstants.FOLDER_NAME, true, "The name of the folder which will be created under the test cycle. E.g. results-build-188");
        options.addOption("rf", CliConstants.RECREATE_FOLDER, true, "Recreate or not the folder. E.g. true");
        options.addOption("esc", CliConstants.EXECUTION_STATUS_COLUMN, true, "The column where you keep the test status (SUCCESS/FAILURE). E.g. 6");
        options.addOption("cc", CliConstants.COMMENTS_COLUMN, true, "The column where you keep additional comments " +
                "for your tests, as the link to the test logs. E.g. 11");
    }

    private ZephyrConfig getZephyrConfigFromCli(CommandLine line) {
        if (line.hasOption(CliConstants.USERNAME)) {
            zephyrConfig.setUsername(line.getOptionValue(CliConstants.USERNAME));
        }

        if (line.hasOption(CliConstants.PASSWORD)) {
            zephyrConfig.setPassword(line.getOptionValue(CliConstants.PASSWORD));
        }

        if (line.hasOption(CliConstants.JIRA_URL)) {
            zephyrConfig.setJiraUrl(line.getOptionValue(CliConstants.JIRA_URL));
        }

        if (line.hasOption(CliConstants.PROJECT_KEY)) {
            zephyrConfig.setProjectKey(line.getOptionValue(CliConstants.PROJECT_KEY));
        }

        if (line.hasOption(CliConstants.RELEASE_VERSION)) {
            zephyrConfig.setReleaseVersion(line.getOptionValue(CliConstants.RELEASE_VERSION));
        }

        if (line.hasOption(CliConstants.TEST_CYCLE)) {
            zephyrConfig.setTestCycle(line.getOptionValue(CliConstants.TEST_CYCLE));
        }

        if (line.hasOption(CliConstants.REPORT_PATH)) {
            zephyrConfig.setReportPath(line.getOptionValue(CliConstants.REPORT_PATH));
        }


        if (line.hasOption(CliConstants.FOLDER_NAME)) {
            zephyrConfig.setFolderName(line.getOptionValue(CliConstants.FOLDER_NAME));
        }

        if (line.hasOption(CliConstants.NO_OF_THREADS)) {
            zephyrConfig.setNoOfThreads(Integer.parseInt(line.getOptionValue(CliConstants.NO_OF_THREADS)));
        }

        if (line.hasOption(CliConstants.RECREATE_FOLDER)) {
            zephyrConfig.setRecreateFolder(Boolean.parseBoolean(line.getOptionValue(CliConstants.RECREATE_FOLDER)));
        }

        if (line.hasOption(CliConstants.EXECUTION_STATUS_COLUMN)) {
            zephyrConfig.setExecutionStatusColumn(Integer.parseInt(line.getOptionValue(CliConstants.EXECUTION_STATUS_COLUMN)));
        }

        if (line.hasOption(CliConstants.COMMENTS_COLUMN)) {
            zephyrConfig.setCommentsColumn(Integer.parseInt(line.getOptionValue(CliConstants.COMMENTS_COLUMN)));
        }

        return zephyrConfig;
    }

    public Options getOptions() {
        return options;
    }
}
