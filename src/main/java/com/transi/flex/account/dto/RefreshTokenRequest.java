package com.transi.flex.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    private String token;

    private Long companyId;
}