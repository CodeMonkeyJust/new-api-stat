package com.newapi.analyzer.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 令牌来源与泄露风险分析请求。
 */
@Data
public class TokenSecurityRequest {

    @NotBlank(message = "开始日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "开始日期格式必须为 YYYY-MM-DD")
    private String startDate;

    @NotBlank(message = "结束日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "结束日期格式必须为 YYYY-MM-DD")
    private String endDate;

    @Size(max = 100, message = "一次最多选择 100 个用户")
    private List<@NotBlank(message = "用户名不能为空") @Size(max = 128, message = "用户名不能超过 128 个字符") String> usernames;

    @Min(value = 1, message = "topN 必须大于 0")
    @Max(value = 500, message = "topN 不能大于 500")
    private Integer topN = 100;

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
