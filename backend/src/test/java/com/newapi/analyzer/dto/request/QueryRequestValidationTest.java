package com.newapi.analyzer.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class QueryRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private QueryRequest validRequest() {
        QueryRequest request = new QueryRequest();
        request.setStartDate("2026-01-01");
        request.setEndDate("2026-01-07");
        request.setDimension("user");
        request.setRankType("total_cost");
        request.setTopN(20);
        return request;
    }

    private Set<String> violations(QueryRequest request) {
        return validator.validate(request).stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toSet());
    }

    @Test
    void validRequestPasses() {
        assertThat(violations(validRequest())).isEmpty();
    }

    @Test
    void startDateAfterEndDateIsRejected() {
        QueryRequest request = validRequest();
        request.setStartDate("2026-02-01");
        request.setEndDate("2026-01-07");
        assertThat(violations(request)).contains("开始日期不能晚于结束日期，且日期范围不能超过 366 天");
    }

    @Test
    void rangeLongerThan366DaysIsRejected() {
        QueryRequest request = validRequest();
        request.setStartDate("2025-01-01");
        request.setEndDate("2026-01-07");
        assertThat(violations(request)).contains("开始日期不能晚于结束日期，且日期范围不能超过 366 天");
    }

    @Test
    void invalidRankTypeIsRejected() {
        QueryRequest request = validRequest();
        request.setRankType("unknown");
        assertThat(violations(request)).contains("排行类型无效");
    }

    @Test
    void topNOutOfRangeIsRejected() {
        QueryRequest request = validRequest();
        request.setTopN(0);
        assertThat(violations(request)).contains("topN 必须大于 0");

        request.setTopN(1001);
        assertThat(violations(request)).contains("topN 不能大于 1000");
    }
}
