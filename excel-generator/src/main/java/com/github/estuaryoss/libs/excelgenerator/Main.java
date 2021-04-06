package com.github.estuaryoss.libs.excelgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.libs.excelgenerator.env.Environment;
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

import static com.github.estuaryoss.libs.excelgenerator.constants.CliConstants.*;

public class Main {
    public static final int ERROR = 1;
    public static final int SUCCESS = 0;

    public static void main(String[] args) throws Exception {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);

        Environment environment = new Environment();

        CliParser commandParser = new CliParser();
        commandParser.parseCommand(args);

        if (commandParser.getInputFilePath() == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    ARTIFACT_NAME + ".jar Main -" +
                    INFILE + " <input.json> -" +
                    OUTFILE + " <output.xls> -", commandParser.getOptions());
            System.exit(ERROR);
        }

        if (commandParser.getOutputFilePath() == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp " +
                    ARTIFACT_NAME + ".jar Main -" +
                    INFILE + " <input.json> -" +
                    OUTFILE + " <output.xls> -", commandParser.getOptions());
            System.exit(ERROR);
        }

        File file = new File(commandParser.getInputFilePath());
        String fileContent = "";
        try (InputStream inputStream = new FileInputStream(file)) {
            fileContent = IOUtils.toString(inputStream, "UTF-8");
        } catch (Exception e) {
            rootLogger.error(ExceptionUtils.getStackTrace(e));
            System.exit(ERROR);
        }

        /* will try to deserialize to a list of messages */
        List<LinkedHashMap<String, Object>> multipleMesages = null;
        try {
            multipleMesages = new ObjectMapper().readValue(fileContent, List.class);
        } catch (Exception e) {
            rootLogger.error("Could not deserialize to a List. \n" + ExceptionUtils.getStackTrace(e));
            System.exit(ERROR);
        }

        ExcelWriter excelWriter = new ExcelWriter();
        try {
            excelWriter.writeExcel(multipleMesages, commandParser.getOutputFilePath());
        } catch (Exception e) {
            rootLogger.error("Error while writing the Excel:\n" + ExceptionUtils.getStackTrace(e));
            System.exit(ERROR);
        }

        System.exit(SUCCESS);
    }
}
