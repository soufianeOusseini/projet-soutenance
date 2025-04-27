package com.transi.flex.account.dto;

import lombok.Getter;
import lombok.Setter;
import com.transi.flex.setting.enums.Language;

@Getter
@Setter
public class ProfileUpdateDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Language defaultLanguage;
}