package com.github.estuaryoss.libs.fluentdlogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.libs.fluentdlogger.constants.CliConstants;
import com.github.estuaryoss.libs.fluentdlogger.constants.EnvConstants;
import com.github.estuaryoss.libs.fluentdlogger.env.Environment;
import com.github.estuaryoss.libs.fluentdlogger.service.FluentdService;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {
    public static final int ERROR = 1;
    public static final int SUCCESS = 0;

    public static void main(String[] args) throws Exception {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);

        Environment environment = new Environment();

        CliParser commandParser = new CliParser();
        commandParser.parseCommand(args);

        if (commandParser.getTag() == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    CliConstants.ARTIFACT_NAME + ".jar Main -" +
                    CliConstants.TAG + " <fluentd_tag> -" +
                    CliConstants.FLUENTD_IP_PORT + " <fluentd_ip_port> -" +
                    CliConstants.FILE + " <file_path>", commandParser.getOptions());
            System.exit(ERROR);
        }

        if (commandParser.getFilePath() == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    CliConstants.ARTIFACT_NAME + ".jar Main -" +
                    CliConstants.TAG + " <fluentd_tag> -" +
                    CliConstants.FLUENTD_IP_PORT + " <fluentd_ip_port> -" +
                    CliConstants.FILE + " <file_path>", commandParser.getOptions());
            System.exit(ERROR);
        }

        if (commandParser.getFluentdIpPort() == null &&
                environment.getEnvAndVirtualEnv().get(EnvConstants.FLUENTD_IP_PORT) == null) {
            rootLogger.error("Fluentd ip:port location was not detected in command and neither environment variable " + EnvConstants.FLUENTD_IP_PORT + "\n");
            rootLogger.error("Please set option '-fluentd' in command line interface or set the 'FLUENTD_IP_PORT' environment variable \n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    CliConstants.ARTIFACT_NAME + ".jar Main -" +
                    CliConstants.TAG + " <fluentd_tag> -" +
                    CliConstants.FLUENTD_IP_PORT + " <fluentd_ip_port> -" +
                    CliConstants.FILE + " <file_path>", commandParser.getOptions());
            System.exit(ERROR);
        }
        String fluentdIpPort = commandParser.getFluentdIpPort() != null ? commandParser.getFluentdIpPort() :
                environment.getEnvAndVirtualEnv().get(EnvConstants.FLUENTD_IP_PORT);

        File file = new File(commandParser.getFilePath());
        String fileContent = "";
        try (InputStream inputStream = new FileInputStream(file)) {
            fileContent = IOUtils.toString(inputStream, "UTF-8");
        } catch (Exception e) {
            rootLogger.error(ExceptionUtils.getStackTrace(e));
            System.exit(ERROR);
        }

        /* will try to deserialize to a list of messages or to one single message */
        ObjectMapper objectMapper = new ObjectMapper();
        FluentdService fluentdService = new FluentdService(About.NAME, fluentdIpPort);
        try {
            List<LinkedHashMap<String, Object>> multipleMesages = objectMapper.readValue(fileContent, List.class);
            multipleMesages.forEach(message -> fluentdService.emit(commandParser.getTag(), message));
        } catch (Exception e) {
            try {
                LinkedHashMap oneMessage = objectMapper.readValue(fileContent, LinkedHashMap.class);
                fluentdService.emit(commandParser.getTag(), (LinkedHashMap<String, Object>) oneMessage);
            } catch (Exception e1) {
                rootLogger.error("Could not deserialize to a List or a Map. \n" + ExceptionUtils.getStackTrace(e1));
                System.exit(ERROR);
            }
        }

        System.exit(SUCCESS);
    }
}
