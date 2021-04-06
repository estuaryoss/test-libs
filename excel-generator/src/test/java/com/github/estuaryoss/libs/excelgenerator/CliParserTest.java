package com.github.estuaryoss.libs.excelgenerator;

import org.apache.commons.cli.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CliParserTest {
    @DataProvider(name = "invalidArgsThrows")
    public Object[][] invalidArgsThrowsData() {
        return new Object[][]{
                {"ls -lrt ", "-lrt"}
        };
    }

    @Test(dataProvider = "invalidArgsThrows")
    public void whenUnrecognizedArgsArePassedItThrowsParseException(String command, String expectedValue) {
        CliParser commandParser = new CliParser();

        assertThatThrownBy(() -> {
            commandParser.parseCommand(command.split(" "));
        }).isInstanceOf(ParseException.class)
                .hasMessageContaining("Unrecognized option: " + expectedValue);

    }

    @DataProvider(name = "invalidArgs")
    public Object[][] invalidArgsData() {
        return new Object[][]{
                {"echo 1"}
        };
    }

    @Test(dataProvider = "invalidArgs")

    public void whenUnrecognizedArgsArePassedThenArgsAreNull(String command) throws ParseException {
        CliParser commandParser = new CliParser();

        commandParser.parseCommand(command.split(" "));
        assertThat(commandParser.getInputFilePath()).isEqualTo(null);
        assertThat(commandParser.getOutputFilePath()).isEqualTo(null);
        assertThat(commandParser.getOptions().toString()).isNotEqualTo(null);
    }

    @DataProvider(name = "multipleArgs")
    public Object[][] multipleArgsData() {
        return new Object[][]{
                {"-infile results1.json -outfile what.xls", new String[]{"results1.json", "what.xls"}},
                {"-infile results22.json -outfile Regression.xlsx", new String[]{"results22.json", "Regression.xlsx"}},
        };
    }

    @Test(dataProvider = "multipleArgs")
    public void whenMultipleArgsAreServedThenTheDetailsAreParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getInputFilePath()).isEqualTo(expectedValues[0]);
        assertThat(commandParser.getOutputFilePath()).isEqualTo(expectedValues[1]);
    }

    @DataProvider(name = "customCommandsMultipleArgsData")
    public Object[][] customCommandsMultipleArgs() {
        return new Object[][]{
                {"-infile results1.json -outfile results.xls", new String[]{"results1.json", "results.xls"}},
                {"-infile results2.json -outfile Whatever.xls", new String[]{"results2.json", "Whatever.xls"}},
        };
    }

    @Test(dataProvider = "customCommandsMultipleArgsData")
    public void whenCommandIsServedThenTheDetailsAreParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getInputFilePath()).isEqualTo(expectedValues[0]);
        assertThat(commandParser.getOutputFilePath()).isEqualTo(expectedValues[1]);
    }

    @DataProvider(name = "brokenWithSpacesCustomCommands")
    public Object[][] brokenCustomCommandsData() {
        return new Object[][]{
                {"-infile       results.json -outfile  results.xlsx", new String[]{"results.json", "results.xlsx"}},
                {"-infile  results.json -outfile     results.xls", new String[]{"results.json", "results.xls"}}
        };
    }

    @Test(dataProvider = "brokenWithSpacesCustomCommands")
    public void whenBrokenCommandIsServedThenTheArgumentsAreNotParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getInputFilePath()).isNotEqualTo(expectedValues[0]);
        assertThat(commandParser.getOutputFilePath()).isNotEqualTo(expectedValues[1]);
    }
}
