package com.github.estuaryoss.libs.fluentdlogger;

import org.apache.commons.cli.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CliParserTest {
    @DataProvider(name = "systemCommands")
    public Object[][] systemCommandsData() {
        return new Object[][]{
                {"ls -lrt", "-lrt"},
                {"ls -lrt | grep README.md", "-lrt"}
        };
    }

    @Test(dataProvider = "systemCommands")
    public void whenUnrecognizedCommandIsPassedItThrowsParseException(String command, String expectedValue) {
        CliParser commandParser = new CliParser();

        assertThatThrownBy(() -> {
            commandParser.parseCommand(command.split(" "));
        }).isInstanceOf(ParseException.class)
                .hasMessageContaining("Unrecognized option: " + expectedValue);

    }

    @DataProvider(name = "customCommands")
    public Object[][] customCommandsData() {
        return new Object[][]{
                {"-tag tag -file README.md", new String[]{"tag", "README.md"}}
        };
    }

    @Test(dataProvider = "customCommands")
    public void whenCustomCommandIsServedThenTheDetailsAreParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getTag()).isEqualTo(expectedValues[0]);
        assertThat(commandParser.getFilePath()).isEqualTo(expectedValues[1]);
    }

    @DataProvider(name = "customCommandsMultipleArgs")
    public Object[][] customCommandsMultipleArgsData() {
        return new Object[][]{
                {"-tag tag1 -file README.md", new String[]{"tag1", "README.md"}},
                {"-tag tag2 -file Whatever.txt", new String[]{"tag2", "Whatever.txt"}},
        };
    }

    @Test(dataProvider = "customCommandsMultipleArgs")
    public void whenCustomCommandWithMultipleArgsIsServedThenTheDetailsAreParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getTag()).isEqualTo(expectedValues[0]);
        assertThat(commandParser.getFilePath()).isEqualTo(expectedValues[1]);
        assertThat(commandParser.getFluentdIpPort()).isEqualTo(null);
    }

    @DataProvider(name = "customCommandsMultipleArgsWithFluentd")
    public Object[][] customCommandsMultipleArgsDataWithFluentd() {
        return new Object[][]{
                {"-tag tag1 -file README.md -fluentd localhost:24224", new String[]{"tag1", "README.md", "localhost:24224"}},
                {"-tag tag2 -file Whatever.txt -fluentd 192.168.10.1:24224", new String[]{"tag2", "Whatever.txt", "192.168.10.1:24224"}},
        };
    }

    @Test(dataProvider = "customCommandsMultipleArgsWithFluentd")
    public void whenCustomCommandWithFluentdIpPortIsServedThenTheDetailsAreParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getTag()).isEqualTo(expectedValues[0]);
        assertThat(commandParser.getFilePath()).isEqualTo(expectedValues[1]);
        assertThat(commandParser.getFluentdIpPort()).isEqualTo(expectedValues[2]);
    }

    @DataProvider(name = "brokenWithSpacesCustomCommands")
    public Object[][] brokenCustomCommandsData() {
        return new Object[][]{
                {"-tag       tag1 -file  README.md", new String[]{"tag1", "README.md"}},
                {"-tag  tag2 -file     README.md", new String[]{"tag2", "README.md"}}
        };
    }

    @Test(dataProvider = "brokenWithSpacesCustomCommands")
    public void whenBrokenCustomCommandIsServedThenTheCommandAndArgumentAreNotParsedOK(String command, String[] expectedValues) throws ParseException {
        CliParser commandParser = new CliParser();
        commandParser.parseCommand(command.split(" "));

        assertThat(commandParser.getTag()).isNotEqualTo(expectedValues[0]);
        assertThat(commandParser.getFilePath()).isNotEqualTo(expectedValues[1]);
    }
}
