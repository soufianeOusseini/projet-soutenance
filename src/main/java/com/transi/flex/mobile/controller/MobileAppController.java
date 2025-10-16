package com.transi.flex.mobile.controller;

import com.transi.flex.mobile.dto.MobileApp;
import com.transi.flex.mobile.service.MobileAppService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile-app")
@RequiredArgsConstructor
public class MobileAppController {

    private final MobileAppService service;

    @GetMapping()
    public MobileApp getMobileAppState(){
       return service.getMobileAppState();
    }
}
