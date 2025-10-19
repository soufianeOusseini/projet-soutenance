package com.transi.flex.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;
}
