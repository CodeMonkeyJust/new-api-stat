package com.newapi.analyzer.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalStatsRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private PersonalStatsRequest validRequest() {
        PersonalStatsRequest request = new PersonalStatsRequest();
        request.setStartDate("2026-01-01");
        request.setEndDate("2026-01-07");
        return request;
    }

    private Set<String> violations(PersonalStatsRequest request) {
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
        PersonalStatsRequest request = validRequest();
        request.setStartDate("2026-02-01");
        request.setEndDate("2026-01-07");
        assertThat(violations(request)).contains("开始日期不能晚于结束日期，且日期范围不能超过 366 天");
    }

    @Test
    void rangeLongerThan366DaysIsRejected() {
        PersonalStatsRequest request = validRequest();
        request.setStartDate("2025-01-01");
        request.setEndDate("2026-01-07");
        assertThat(violations(request)).contains("开始日期不能晚于结束日期，且日期范围不能超过 366 天");
    }

    @Test
    void blankDatesAreRejected() {
        PersonalStatsRequest request = validRequest();
        request.setStartDate("");
        assertThat(violations(request)).contains("开始日期不能为空");
    }

    @Test
    void invalidDateFormatIsRejected() {
        PersonalStatsRequest request = validRequest();
        request.setStartDate("2026/01/01");
        assertThat(violations(request)).contains("开始日期格式必须为 YYYY-MM-DD");
    }
}
