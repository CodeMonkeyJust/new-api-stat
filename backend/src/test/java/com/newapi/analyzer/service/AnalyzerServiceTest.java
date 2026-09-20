package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.QueryRequest;
import com.newapi.analyzer.dto.response.ModelDailyResponse;
import com.newapi.analyzer.repository.LogRepository;
import com.newapi.analyzer.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnalyzerServiceTest {

    @Test
    void modelDailyAggregatesTheWholeDateRangeByModel() {
        EntityManager entityManager = mock(EntityManager.class);
        Query query = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{"gpt-4o", 120L, 80L, 200L, 0.5D, 3L});
        when(query.getResultList()).thenReturn(rows);

        AnalyzerService analyzerService = new AnalyzerService(
                entityManager,
                mock(LogRepository.class),
                mock(UserRepository.class),
                "Asia/Shanghai",
                "jdbc:mysql://localhost:3306/new_api"
        );

        QueryRequest request = new QueryRequest();
        request.setStartDate("2026-09-01");
        request.setEndDate("2026-09-03");
        request.setDimension("model");
        request.setRankType("total_cost");
        request.setTopN(100);

        List<ModelDailyResponse> result = analyzerService.getModelDaily(request);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue())
                .contains("GROUP BY model_name")
                .doesNotContain("DATE_FORMAT(")
                .doesNotContain("to_timestamp(");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDate()).isEqualTo("2026-09-01 ~ 2026-09-03");
        assertThat(result.get(0).getModel()).isEqualTo("gpt-4o");
        assertThat(result.get(0).getTotalTokens()).isEqualTo(200L);
        assertThat(result.get(0).getCost()).isEqualTo(0.5D);
    }
}
