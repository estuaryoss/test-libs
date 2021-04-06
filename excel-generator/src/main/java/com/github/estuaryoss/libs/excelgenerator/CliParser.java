package com.github.estuaryoss.libs.excelgenerator;

import org.apache.commons.cli.*;

import static com.github.estuaryoss.libs.excelgenerator.constants.CliConstants.INFILE;
import static com.github.estuaryoss.libs.excelgenerator.constants.CliConstants.OUTFILE;

public class CliParser {
    private String inputFilePath;
    private String outputFilePath;
    private Options options = new Options();
    private CommandLineParser parser = new DefaultParser();

    public void parseCommand(String[] args) throws ParseException {
        options.addOption(INFILE, true, "The input file to be used for report generator. E.g. results.json");
        options.addOption(OUTFILE, true, "The desired output file name. The default value is 'results.xls'. E.g. Regression_20.xlsx");

        CommandLine line = parser.parse(options, args);

        if (line.hasOption(INFILE)) {
            inputFilePath = line.getOptionValue(INFILE);
        }

        if (line.hasOption(OUTFILE)) {
            outputFilePath = line.getOptionValue(OUTFILE);
        }
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public Options getOptions() {
        return options;
    }
}
