package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.QueryRequest;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ExportService {

    public void exportToExcel(QueryRequest request, HttpServletResponse response, Object data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("数据导出");

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        String rankType = request.getRankType();
        String[] headers;

        switch (rankType) {
            case "daily":
                headers = new String[]{"日期", "输入Token", "输出Token", "总Token", "花费(美元)", "调用次数"};
                break;
            case "hourly":
                headers = new String[]{"小时", "输入Token", "输出Token", "总Token", "花费(美元)", "调用次数", "用户数"};
                break;
            default:
                headers = new String[]{"名称", "输入Token", "输出Token", "总Token", "花费(美元)", "调用次数"};
        }

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        if (data instanceof java.util.List) {
            java.util.List<?> dataList = (java.util.List<?>) data;
            for (Object item : dataList) {
                Row row = sheet.createRow(rowNum++);
                Object[] values = extractValues(item, rankType);
                for (int i = 0; i < values.length; i++) {
                    Cell cell = row.createCell(i);
                    if (values[i] instanceof Number) {
                        if (values[i] instanceof Double) {
                            cell.setCellValue((Double) values[i]);
                        } else {
                            cell.setCellValue(((Number) values[i]).longValue());
                        }
                    } else {
                        cell.setCellValue(values[i] != null ? values[i].toString() : "");
                    }
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "export_" + System.currentTimeMillis() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private Object[] extractValues(Object item, String rankType) {
        if (item == null) {
            return new Object[0];
        }

        switch (rankType) {
            case "daily":
                return extractDailyValues(item);
            case "hourly":
                return extractHourlyValues(item);
            default:
                return extractRankValues(item);
        }
    }

    private Object[] extractRankValues(Object item) {
        try {
            java.lang.reflect.Field[] fields = item.getClass().getDeclaredFields();
            Object[] values = new Object[6];
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(item);
                switch (fieldName) {
                    case "name":
                        values[0] = value;
                        break;
                    case "promptTokens":
                        values[1] = value;
                        break;
                    case "completionTokens":
                        values[2] = value;
                        break;
                    case "value":
                        values[3] = value;
                        break;
                    case "cost":
                        values[4] = value;
                        break;
                    case "count":
                        values[5] = value;
                        break;
                }
            }
            return values;
        } catch (Exception e) {
            return new Object[0];
        }
    }

    private Object[] extractDailyValues(Object item) {
        try {
            java.lang.reflect.Field[] fields = item.getClass().getDeclaredFields();
            Object[] values = new Object[6];
            Long promptTokens = 0L;
            Long completionTokens = 0L;
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(item);
                switch (fieldName) {
                    case "date":
                        values[0] = value;
                        break;
                    case "promptTokens":
                        values[1] = value;
                        promptTokens = (Long) value;
                        break;
                    case "completionTokens":
                        values[2] = value;
                        completionTokens = (Long) value;
                        break;
                    case "cost":
                        values[4] = value;
                        break;
                    case "count":
                        values[5] = value;
                        break;
                }
            }
            values[3] = promptTokens + completionTokens;
            return values;
        } catch (Exception e) {
            return new Object[0];
        }
    }

    private Object[] extractHourlyValues(Object item) {
        try {
            java.lang.reflect.Field[] fields = item.getClass().getDeclaredFields();
            Object[] values = new Object[7];
            Long promptTokens = 0L;
            Long completionTokens = 0L;
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(item);
                switch (fieldName) {
                    case "hour":
                        values[0] = value;
                        break;
                    case "promptTokens":
                        values[1] = value;
                        promptTokens = (Long) value;
                        break;
                    case "completionTokens":
                        values[2] = value;
                        completionTokens = (Long) value;
                        break;
                    case "cost":
                        values[4] = value;
                        break;
                    case "count":
                        values[5] = value;
                        break;
                    case "users":
                        values[6] = value;
                        break;
                }
            }
            values[3] = promptTokens + completionTokens;
            return values;
        } catch (Exception e) {
            return new Object[0];
        }
    }
}
