### Description

Fluentd logging library used to support standardized testing.

### Call example

```bash
java -cp target/fluentd-logger-1.0-SNAPSHOT-jar-with-dependencies.jar "com.github.estuaryoss.libs.fluentdlogger.Main" -tag tag -file testResults.json -fluentd 127.0.0.1:24224
```

## Artifact maven central

```xml

<dependency>
  <groupId>com.github.estuaryoss.libs</groupId>
  <artifactId>fluentd-logger</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### Set Fluentd IP:PORT location

There are 3 ways to set the location of fluentd IP:PORT:

- Add an 'environment.properties' file containing the location of the fluentd service. E.g. FLUENTD_IP_PORT=localhost:
  24224
- Set fluentd IP:PORT using an env VAR. E.g. export FLUENTD_IP_PORT=localhost:24224
- Set fluentd IP:PORT using the option '-fluentd' from the Main class of the jar for jar invocation

### Supported formats

## Map - one single test result (example)

```json
{"testName": "exampleTest", "Db": "Mysql57", "OS":"Centos7", "logLocation": "http://logdatabase.com/exampleTest", 
"startedat":  "Sun Nov  1 10:16:52 EET 2020", "endedat":  "Sun Nov  1 10:22:52 EET 2020", ...otherinformation}
```

## List of Map(s) - multiple test result (example)

```json
[
{"testName": "exampleTest1", "Db": "Mysql57", "OS":"Centos7", "logLocation": "http://logdatabase.com/exampleTest1", 
"startedat":  "Sun Nov  1 10:16:52 EET 2020", "endedat":  "Sun Nov  1 10:22:52 EET 2020", ...otherinformation},
{"testName": "exampleTest2", "Db": "Mysql57", "OS":"Centos7", "logLocation": "http://logdatabase.com/exampleTest2", 
"startedat":  "Sun Nov  1 10:22:52 EET 2020", "endedat":  "Sun Nov  1 10:30:52 EET 2020", ...otherinformation}
... other tests

]
```

### Programmatic usage

## One single test

```java
Map<String, Object> map = new LinkedHashMap<String, Object>;
map.put("testName", "exampleTest1");
map.put("Db", "Mysql57");
...
FluentdService fluentdService = new FluentdService(tag, fluentdIpPort);
fluentdService.emit("INFO", map);
```

## Multiple tests

```java
List<LinkedHashMap<String, Object>> multipleMesages = new ArrayList<LinkedHashMap<String,Object>>();
multipleMessages.add(map1);
multipleMessages.add(map2);
...
FluentdService fluentdService = new FluentdService(tag, fluentdIpPort);
multipleMesages.forEach(message -> fluentdService.emit("INFO", message));
```

## Get tests from file using Jackson

**Example 1 (single message)**:

```java
try (InputStream inputStream = new FileInputStream(file)) {
    fileContent = IOUtils.toString(inputStream, "UTF-8");
} catch (Exception e) {
    //put something here
}
LinkedHashMap oneMessage = objectMapper.readValue(fileContent, LinkedHashMap.class);
fluentdService.emit("INFO", (LinkedHashMap<String, Object>) oneMessage);
```

**Example 2 (multiple messages)**:

```
try (InputStream inputStream = new FileInputStream(file)) {
    fileContent = IOUtils.toString(inputStream, "UTF-8");
} catch (Exception e) {
    //put something here
}
List<LinkedHashMap<String, Object>> multipleMesages = objectMapper.readValue(fileContent, List.class);
multipleMesages.forEach(message -> fluentdService.emit("INFO", message));
```
