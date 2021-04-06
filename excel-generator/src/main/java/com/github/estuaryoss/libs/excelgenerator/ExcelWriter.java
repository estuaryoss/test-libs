package com.github.estuaryoss.libs.excelgenerator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelWriter {
    private String sheetName = "Sheet1";

    private Workbook getExcelWorkbook(String excelFilePath) {
        Workbook workbook;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
        sheetName = excelFilePath.substring(0, excelFilePath.indexOf(".xls"));

        return workbook;
    }

    public void writeExcel(List<LinkedHashMap<String, Object>> messages, String excelFilePath) throws IOException {
        Workbook workbook = getExcelWorkbook(excelFilePath);
        Sheet sheet = workbook.createSheet();
        workbook.setSheetName(workbook.getSheetIndex(sheet), sheetName);

        int rowCount = 0;
        Row headerRow = sheet.createRow(rowCount);
        writeHeader(messages.get(0), headerRow);

        for (LinkedHashMap<String, Object> message : messages) {
            Row row = sheet.createRow(++rowCount);
            writeMessage(message, row);
        }

        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
        }
    }

    private void writeMessage(LinkedHashMap<String, Object> message, Row row) {
        int cellPos = 0;
        Cell cell;
        for (Map.Entry<String, Object> entry : message.entrySet()) {
            Object value = entry.getValue();
            cell = row.createCell(cellPos);
            cell.setCellValue(String.valueOf(value));
            cellPos++;
        }
    }

    private void writeHeader(LinkedHashMap<String, Object> message, Row row) {
        int cellPos = 0;
        Cell cell;
        for (Map.Entry<String, Object> entry : message.entrySet()) {
            String key = entry.getKey();
            cell = row.createCell(cellPos);
            cell.setCellValue(key);
            cellPos++;
        }
    }
}
