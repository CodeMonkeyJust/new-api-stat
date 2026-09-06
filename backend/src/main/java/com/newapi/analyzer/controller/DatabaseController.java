package com.newapi.analyzer.controller;

import com.newapi.analyzer.dto.response.ApiResponse;
import com.newapi.analyzer.service.DatabaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/database")
@Tag(name = "数据库管理", description = "数据库连接相关接口")
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/status")
    @Operation(summary = "获取数据库连接状态")
    public ApiResponse<Boolean> status() {
        return ApiResponse.success(databaseService.isConnected());
    }
}
