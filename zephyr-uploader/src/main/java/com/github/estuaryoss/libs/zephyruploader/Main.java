package com.github.estuaryoss.libs.zephyruploader;

import com.github.estuaryoss.libs.zephyruploader.component.ZephyrConfig;
import com.github.estuaryoss.libs.zephyruploader.component.ZephyrUploader;
import com.github.estuaryoss.libs.zephyruploader.config.ApplicationConfig;
import com.github.estuaryoss.libs.zephyruploader.constants.ZephyrParams;
import com.github.estuaryoss.libs.zephyruploader.service.ZephyrService;
import com.github.estuaryoss.libs.zephyruploader.utils.ExcelReader;
import com.github.estuaryoss.libs.zephyruploader.utils.ZephyrConfigValidator;
import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    private static final int ERROR = 1;
    private static final int SUCCESS = 0;
    private static final String ARTIFACT_NAME = "zephyr-uploader";

    public static void main(String[] args) throws Exception {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);

        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        CliParser cliParser = new CliParser();
        ZephyrConfig zephyrConfig = cliParser.parseCommand(args);

        if (!isZephyrConfigFilled(zephyrConfig)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    ARTIFACT_NAME + ".jar Main -" +
                    ZephyrParams.USERNAME.getZephyrParam() + String.format(" <%s> -", ZephyrParams.USERNAME.getZephyrParam()) +
                    ZephyrParams.PASSWORD.getZephyrParam() + String.format(" <%s> -", ZephyrParams.PASSWORD.getZephyrParam()) +
                    ZephyrParams.JIRA_URL.getZephyrParam() + String.format(" <%s> -", ZephyrParams.JIRA_URL.getZephyrParam()) +
                    ZephyrParams.PROJECT_KEY.getZephyrParam() + String.format(" <%s> -", ZephyrParams.PROJECT_KEY.getZephyrParam()) +
                    ZephyrParams.RELEASE_VERSION.getZephyrParam() + String.format(" <%s> -", ZephyrParams.RELEASE_VERSION.getZephyrParam()) +
                    ZephyrParams.TEST_CYCLE.getZephyrParam() + String.format(" <%s> -", ZephyrParams.TEST_CYCLE.getZephyrParam()) +
                    ZephyrParams.NO_OF_THREADS.getZephyrParam() + String.format(" <%s> -", ZephyrParams.NO_OF_THREADS.getZephyrParam()) +
                    ZephyrParams.RECREATE_FOLDER.getZephyrParam() + String.format(" <%s> -", ZephyrParams.RECREATE_FOLDER.getZephyrParam()) +
                    ZephyrParams.REPORT_PATH.getZephyrParam() + String.format(" <%s> -", ZephyrParams.REPORT_PATH.getZephyrParam()) +
                    ZephyrParams.EXECUTION_STATUS_COLUMN.getZephyrParam() + String.format(" <%s> -", ZephyrParams.EXECUTION_STATUS_COLUMN.getZephyrParam()) +
                    ZephyrParams.COMMENTS_COLUMN.getZephyrParam() + String.format(" <%s>", ZephyrParams.COMMENTS_COLUMN.getZephyrParam()), cliParser.getOptions());
            ZephyrConfigValidator.validate(zephyrConfig);

            System.exit(ERROR);
        }


        ZephyrConfigValidator.validate(zephyrConfig);

        ZephyrService zephyrService = context.getBean(ZephyrService.class);
        zephyrService.setZephyrConfig(zephyrConfig);

        ZephyrUploader zephyrUploader = context.getBean(ZephyrUploader.class);

        String[][] rawExcelData = ExcelReader.readExcel(zephyrConfig.getReportPath());
        zephyrUploader.updateJiraZephyr(rawExcelData);

        System.exit(SUCCESS);
    }

    private static boolean isZephyrConfigFilled(ZephyrConfig config) {
        if (config.getJiraUrl() == null || config.getUsername() == null || config.getPassword() == null ||
                config.getProjectKey() == null || config.getReleaseVersion() == null || config.getTestCycle() == null ||
                config.getTestCycle() == null) {
            return false;
        }

        return true;
    }
}
