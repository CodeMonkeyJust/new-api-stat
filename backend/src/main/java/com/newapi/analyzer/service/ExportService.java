package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.QueryRequest;
import com.newapi.analyzer.dto.response.HourlyResponse;
import com.newapi.analyzer.dto.response.ModelDailyResponse;
import com.newapi.analyzer.dto.response.PersonalModelResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ExportService {

    public void exportToExcel(QueryRequest request, HttpServletResponse response, Object data) throws IOException {
        String rankType = request.getRankType();
        String[] headers;
        Function<Object, Object[]> valueExtractor;

        switch (rankType) {
            case "daily":
                headers = new String[]{"日期", "输入Token", "输出Token", "总Token", "花费(美元)", "调用次数"};
                valueExtractor = this::extractDailyValues;
                break;
            case "hourly":
                headers = new String[]{"小时", "输入Token", "输出Token", "总Token", "花费(美元)", "调用次数", "用户数"};
                valueExtractor = this::extractHourlyValues;
                break;
            default:
                headers = new String[]{"名称", "输入Token", "输出Token", "总Token", "花费(美元)", "调用次数"};
                valueExtractor = this::extractRankValues;
        }

        writeWorkbook(response, "数据导出", headers, data, valueExtractor);
    }

    public void exportUserHourlyToExcel(HttpServletResponse response, Object data) throws IOException {
        String[] headers = new String[]{"时间", "输入Token", "输出Token", "费用(美元)"};
        writeWorkbook(response, "用户24小时统计", headers, normalizeHourlyRows(data), this::extractUserHourlyValues);
    }

    public void exportUserModelsToExcel(HttpServletResponse response, Object data) throws IOException {
        String[] headers = new String[]{"模型", "输入Token", "输出Token", "费用(美元)", "调用次数"};
        writeWorkbook(response, "模型使用统计", headers, data, this::extractUserModelValues);
    }

    public void exportPersonalHourlyToExcel(HttpServletResponse response, Object data) throws IOException {
        String[] headers = new String[]{"时间", "输入Token", "输出Token", "总Token", "费用(美元)", "调用次数"};
        writeWorkbook(response, "个人时间段统计", headers, normalizeHourlyRows(data), this::extractPersonalHourlyValues);
    }

    public void exportPersonalModelsToExcel(HttpServletResponse response, Object data) throws IOException {
        String[] headers = new String[]{"模型", "输入Token", "输出Token", "总Token", "费用(美元)", "调用次数"};
        writeWorkbook(response, "个人模型明细", headers, data, this::extractPersonalModelValues);
    }

    private void writeWorkbook(HttpServletResponse response, String sheetName, String[] headers, Object data,
                               Function<Object, Object[]> valueExtractor) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            if (data instanceof List<?> dataList) {
                for (Object item : dataList) {
                    Row row = sheet.createRow(rowNum++);
                    Object[] values = valueExtractor.apply(item);
                    for (int i = 0; i < values.length; i++) {
                        Cell cell = row.createCell(i);
                        if (values[i] instanceof Number number) {
                            if (number instanceof Double) {
                                cell.setCellValue(number.doubleValue());
                            } else {
                                cell.setCellValue(number.longValue());
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
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
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

    private List<HourlyResponse> normalizeHourlyRows(Object data) {
        Map<Integer, HourlyResponse> hourlyByHour = new HashMap<>();

        if (data instanceof List<?> dataList) {
            for (Object item : dataList) {
                if (item instanceof HourlyResponse hourly && hourly.getHour() != null) {
                    hourlyByHour.put(hourly.getHour(), hourly);
                }
            }
        }

        List<HourlyResponse> rows = new ArrayList<>(24);
        for (int hour = 0; hour < 24; hour++) {
            HourlyResponse item = hourlyByHour.get(hour);
            rows.add(item != null ? item : new HourlyResponse(hour, 0L, 0.0, 0L, 0L, 0L, 0L));
        }
        return rows;
    }

    private Object[] extractPersonalHourlyValues(Object item) {
        if (!(item instanceof HourlyResponse hourly)) {
            return new Object[0];
        }

        long promptTokens = hourly.getPromptTokens() != null ? hourly.getPromptTokens() : 0L;
        long completionTokens = hourly.getCompletionTokens() != null ? hourly.getCompletionTokens() : 0L;
        return new Object[]{
                (hourly.getHour() != null ? hourly.getHour() : 0) + ":00",
                promptTokens,
                completionTokens,
                promptTokens + completionTokens,
                hourly.getCost(),
                hourly.getCount()
        };
    }

    private Object[] extractPersonalModelValues(Object item) {
        if (!(item instanceof PersonalModelResponse model)) {
            return new Object[0];
        }

        return new Object[]{
                model.getModel(),
                model.getPromptTokens(),
                model.getCompletionTokens(),
                model.getTotalTokens(),
                model.getCost(),
                model.getCallCount()
        };
    }

    private Object[] extractUserHourlyValues(Object item) {
        if (!(item instanceof HourlyResponse hourly)) {
            return new Object[0];
        }

        return new Object[]{
                (hourly.getHour() != null ? hourly.getHour() : 0) + ":00",
                hourly.getPromptTokens(),
                hourly.getCompletionTokens(),
                hourly.getCost()
        };
    }

    private Object[] extractUserModelValues(Object item) {
        if (!(item instanceof ModelDailyResponse modelUsage)) {
            return new Object[0];
        }

        return new Object[]{
                modelUsage.getModel(),
                modelUsage.getPromptTokens(),
                modelUsage.getCompletionTokens(),
                modelUsage.getCost(),
                modelUsage.getCallCount()
        };
    }
}
