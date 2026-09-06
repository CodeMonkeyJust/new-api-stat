package com.newapi.analyzer.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 个人统计查询请求：仅包含日期范围，用户名由后端从 session 推导。
 */
@Data
public class PersonalStatsRequest {

    @NotBlank(message = "开始日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "开始日期格式必须为 YYYY-MM-DD")
    private String startDate;

    @NotBlank(message = "结束日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "结束日期格式必须为 YYYY-MM-DD")
    private String endDate;

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
