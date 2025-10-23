package com.transi.flex.dashboard.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuperDashboardStatsDTO {

    private Long totalCompanies;
    private Long totalAgencies;
    private Long totalTickets;
    private Double totalSales;
    private List<MonthlySalesDTO> monthlySales;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySalesDTO {
        private String month;
        private Double sales;
    }
}