package com.github.estuaryoss.libs.zephyruploader.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelReader {

    private static Workbook getExcelWorkbook(String excelFilePath) throws IOException {
        Workbook workbook;
        InputStream inputStream = new FileInputStream(excelFilePath);
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    public static String[][] readExcel(String excelFilePath) throws IOException {
        String[][] data;

        Sheet sheet = getExcelWorkbook(excelFilePath).getSheetAt(0); //first sheet
        Row row;
        int nrOfRows = sheet.getPhysicalNumberOfRows();
        int nrOfColumns = sheet.getRow(0).getLastCellNum();
        data = new String[nrOfRows][nrOfColumns];

        for (int i = 0; i < nrOfRows; i++) {
            row = sheet.getRow(i);
            for (int j = 0; j < nrOfColumns; j++) {
                try {
                    data[i][j] = row.getCell(j).getStringCellValue();
                } catch (NullPointerException e) {
                    data[i][j] = "";
                }
            }
        }

        return data;
    }
}
