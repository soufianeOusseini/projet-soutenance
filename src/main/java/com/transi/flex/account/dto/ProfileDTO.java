package com.transi.flex.account.dto;

import lombok.Getter;
import lombok.Setter;
import com.transi.flex.account.enums.UserProfile;

@Getter
@Setter
public class ProfileDTO {

    private Long id;

    private UserProfile name;
}