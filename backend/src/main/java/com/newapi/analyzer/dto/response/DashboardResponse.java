package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private DashboardSummary total;
    private DashboardSummary today;
    private DashboardSummary yesterday;
    private java.util.List<DailyTopUser> last7DaysTopUsers;
}
