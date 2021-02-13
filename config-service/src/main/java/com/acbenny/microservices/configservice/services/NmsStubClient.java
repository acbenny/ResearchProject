package com.acbenny.microservices.configservice.services;

import java.util.ArrayList;

import com.acbenny.microservices.configservice.models.ConfigOperation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("nms-stub")
public interface NmsStubClient {
    @PostMapping("/nms")
    public boolean config(@RequestBody ArrayList<ConfigOperation> opers);
}