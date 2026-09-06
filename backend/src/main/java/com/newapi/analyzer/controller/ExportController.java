package com.newapi.analyzer.controller;

import com.newapi.analyzer.dto.request.QueryRequest;
import com.newapi.analyzer.service.AnalyzerService;
import com.newapi.analyzer.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@Tag(name = "数据导出", description = "数据导出相关接口")
public class ExportController {

    private final AnalyzerService analyzerService;
    private final ExportService exportService;

    public ExportController(AnalyzerService analyzerService, ExportService exportService) {
        this.analyzerService = analyzerService;
        this.exportService = exportService;
    }

    @PostMapping("/data")
    @Operation(summary = "导出数据")
    public void exportData(@Valid @RequestBody QueryRequest request, HttpServletResponse response) throws IOException {
        Object data;
        String rankType = request.getRankType();

        switch (rankType) {
            case "daily":
                data = analyzerService.getDaily(request);
                break;
            case "hourly":
                data = analyzerService.getHourly(request);
                break;
            default:
                data = analyzerService.getRank(request);
        }

        exportService.exportToExcel(request, response, data);
    }
}
