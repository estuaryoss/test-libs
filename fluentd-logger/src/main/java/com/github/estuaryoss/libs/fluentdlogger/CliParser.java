package com.github.estuaryoss.libs.fluentdlogger;

import com.github.estuaryoss.libs.fluentdlogger.constants.CliConstants;
import org.apache.commons.cli.*;

public class CliParser {
    private String tag;
    private String filePath;
    private String fluentdIpPort;
    private Options options = new Options();
    private CommandLineParser parser = new DefaultParser();

    public void parseCommand(String[] args) throws ParseException {
        options.addOption(CliConstants.TAG, true, "Fluentd tag used to log the message. E.g. regression");
        options.addOption(CliConstants.FILE, true, "The json file which contains the message(s). E.g. results.json");
        options.addOption(CliConstants.FLUENTD_IP_PORT, true, "The fluentd instance location in 'ip:port' format. E.g. localhost:24224");

        CommandLine line = parser.parse(options, args);

        if (line.hasOption(CliConstants.TAG)) {
            tag = line.getOptionValue(CliConstants.TAG);
        }

        if (line.hasOption(CliConstants.FILE)) {
            filePath = line.getOptionValue(CliConstants.FILE);
        }

        if (line.hasOption(CliConstants.FLUENTD_IP_PORT)) {
            fluentdIpPort = line.getOptionValue(CliConstants.FLUENTD_IP_PORT);
        }
    }

    public String getTag() {
        return tag;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFluentdIpPort() {
        return fluentdIpPort;
    }

    public Options getOptions() {
        return options;
    }
}
