package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalStatsResponse {
    private String username;
    private String displayName;
    private PersonalSummaryResponse summary;
    private List<PersonalModelResponse> models;
}
