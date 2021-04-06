### Description

Test Report generator in Excel library used to support standardized testing.

### Call example

```bash
java -cp excel-generator-jar-with-dependencies.jar "Main" -infile results.json -outfile testResults.xlsx
```

## Programmatic example

```java
Map<String, Object> map = new LinkedHashMap<String, Object>;
map.put("testName", "exampleTest1");
map.put("Db", "Mysql57");
...
List<LinkedHashMap<String, Object>> multipleMesages = new ArrayList<LinkedHashMap<String,Object>>();
multipleMessages.add(map);

ExcelWriter excelWriter = new ExcelWriter();
String outputFilePath = "Results.xls";
try {
    excelWriter.writeExcel(multipleMesages, outputFilePath);
} catch (Exception e) {
    rootLogger.error("Error while writing the Excel:\n" + ExceptionUtils.getStackTrace(e));
}
```
