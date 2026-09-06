package com.newapi.analyzer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.Data;

import java.util.List;

@Data
public class QueryRequest {
    @NotBlank(message = "开始日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "开始日期格式必须为 YYYY-MM-DD")
    private String startDate;

    @NotBlank(message = "结束日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "结束日期格式必须为 YYYY-MM-DD")
    private String endDate;

    @NotBlank(message = "统计维度不能为空")
    @Pattern(regexp = "user|model|group", message = "统计维度无效")
    private String dimension;

    @NotBlank(message = "排行类型不能为空")
    @Pattern(regexp = "total_cost|call_count|daily|hourly", message = "排行类型无效")
    private String rankType;

    @NotNull(message = "topN 不能为空")
    @Min(value = 1, message = "topN 必须大于 0")
    @Max(value = 1000, message = "topN 不能大于 1000")
    private Integer topN = 10;

    @Size(max = 128, message = "用户名不能超过 128 个字符")
    private String username;

    @Size(max = 100, message = "一次最多选择 100 个用户")
    private List<@NotBlank(message = "用户名不能为空") @Size(max = 128, message = "用户名不能超过 128 个字符") String> usernames;

    @AssertTrue(message = "开始日期不能晚于结束日期，且日期范围不能超过 366 天")
    public boolean isDateRangeValid() {
        try {
            return startDate != null && endDate != null
                    && !LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))
                    && !LocalDate.parse(endDate).isAfter(LocalDate.parse(startDate).plusDays(365));
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}
