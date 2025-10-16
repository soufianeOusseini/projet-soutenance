package com.transi.flex.agency.dto;

@lombok.Data
@lombok.Builder
public class AgencyStatsDTO {

    private int totalAgencies;
    private int activeAgencies;
    private int inactiveAgencies;

}
