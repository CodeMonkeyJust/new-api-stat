package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTopUser {
    private String date;
    private String topCostUser;
    private Double topCostValue;
    private String topPromptTokensUser;
    private Long topPromptTokensValue;
    private String topCompletionTokensUser;
    private Long topCompletionTokensValue;
}
