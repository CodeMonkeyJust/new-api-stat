package com.newapi.analyzer.controller;

import com.newapi.analyzer.dto.request.QueryRequest;
import com.newapi.analyzer.dto.request.PersonalStatsRequest;
import com.newapi.analyzer.dto.response.*;
import com.newapi.analyzer.service.AnalyzerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/analyzer")
@Tag(name = "数据分析", description = "数据分析相关接口")
@Validated
public class AnalyzerController {

    private final AnalyzerService analyzerService;

    public AnalyzerController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping("/summary")
    @Operation(summary = "获取统计汇总")
    public ApiResponse<SummaryResponse> getSummary(@Valid @RequestBody QueryRequest request) {
        SummaryResponse summary = analyzerService.getSummary(request);
        return ApiResponse.success(summary);
    }

    @PostMapping("/rank")
    @Operation(summary = "获取排行数据")
    public ApiResponse<List<RankResponse>> getRank(@Valid @RequestBody QueryRequest request) {
        List<RankResponse> rank = analyzerService.getRank(request);
        return ApiResponse.success(rank);
    }

    @PostMapping("/hourly")
    @Operation(summary = "获取24小时分布")
    public ApiResponse<List<HourlyResponse>> getHourly(@Valid @RequestBody QueryRequest request) {
        List<HourlyResponse> hourly = analyzerService.getHourly(request);
        return ApiResponse.success(hourly);
    }

    @PostMapping("/daily")
    @Operation(summary = "获取每日消耗")
    public ApiResponse<List<DailyResponse>> getDaily(@Valid @RequestBody QueryRequest request) {
        List<DailyResponse> daily = analyzerService.getDaily(request);
        return ApiResponse.success(daily);
    }

    @PostMapping("/model-daily")
    @Operation(summary = "获取模型每日消耗")
    public ApiResponse<List<ModelDailyResponse>> getModelDaily(@Valid @RequestBody QueryRequest request) {
        List<ModelDailyResponse> modelDaily = analyzerService.getModelDaily(request);
        return ApiResponse.success(modelDaily);
    }

    @PostMapping("/hourly-users")
    @Operation(summary = "获取指定小时的用户统计")
    public ApiResponse<List<HourlyUserResponse>> getHourlyUsers(@Valid @RequestBody QueryRequest request, @RequestParam @Min(0) @Max(23) Integer hour) {
        List<HourlyUserResponse> hourlyUsers = analyzerService.getHourlyUsers(request, hour);
        return ApiResponse.success(hourlyUsers);
    }

    @PostMapping("/model-users")
    @Operation(summary = "获取指定模型的用户统计")
    public ApiResponse<List<ModelUserResponse>> getModelUsers(@Valid @RequestBody QueryRequest request, @RequestParam @NotBlank String model) {
        List<ModelUserResponse> modelUsers = analyzerService.getModelUsers(request, model);
        return ApiResponse.success(modelUsers);
    }

    @GetMapping("/user-balances")
    @Operation(summary = "获取用户余额列表")
    public ApiResponse<List<UserBalanceResponse>> getUserBalances() {
        List<UserBalanceResponse> userBalances = analyzerService.getUserBalances();
        return ApiResponse.success(userBalances);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表盘数据")
    public ApiResponse<DashboardResponse> getDashboard() {
        DashboardResponse dashboard = analyzerService.getDashboard();
        return ApiResponse.success(dashboard);
    }

    @GetMapping("/users")
    @Operation(summary = "获取用户列表")
    public ApiResponse<List<UserResponse>> getUsers() {
        List<UserResponse> users = analyzerService.getUsers();
        return ApiResponse.success(users);
    }

    @PostMapping("/user-daily")
    @Operation(summary = "获取用户每日消耗")
    public ApiResponse<List<UserDailyResponse>> getUserDaily(@Valid @RequestBody QueryRequest request) {
        List<UserDailyResponse> userDaily = analyzerService.getUserDaily(request);
        return ApiResponse.success(userDaily);
    }

    @PostMapping("/personal/stats")
    @Operation(summary = "获取个人统计（按模型汇总）")
    public ApiResponse<PersonalStatsResponse> getPersonalStats(@Valid @RequestBody PersonalStatsRequest request, HttpSession session) {
        Object sessionId = session.getAttribute("id");
        if (!(sessionId instanceof Number)) {
            throw new IllegalArgumentException("登录状态无效，请重新登录");
        }
        PersonalStatsResponse stats = analyzerService.getPersonalStats(request, ((Number) sessionId).longValue());
        return ApiResponse.success(stats);
    }
}
