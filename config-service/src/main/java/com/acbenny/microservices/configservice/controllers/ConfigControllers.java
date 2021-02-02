package com.acbenny.microservices.configservice.controllers;

import java.util.ArrayList;

import com.acbenny.microservices.configservice.models.ConfigOperation;
import com.acbenny.microservices.configservice.models.Order;
import com.acbenny.microservices.configservice.services.ConfigService;
import com.acbenny.microservices.configservice.services.NEServiceClient;
import com.acbenny.microservices.configservice.services.OrderServiceClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigControllers {

    @Autowired
    private ConfigService service;

    @Autowired
    public OrderServiceClient ordService;

    @Autowired
    public NEServiceClient neService;

    @GetMapping("/config/{ordId}")
    public ArrayList<ConfigOperation> configOrdStub(@PathVariable long ordId) {
        Order ord = ordService.getOrdDetails(ordId);
        ArrayList<ConfigOperation> opers = new ArrayList<ConfigOperation>();
        ord.getNeIds().forEach(neId -> {
            opers.add(service.generateOperation(ord,neId));
        });
        return opers;
    }

    @GetMapping("/deconfig/{ordId}")
    public ArrayList<ConfigOperation> deconfigOrdStub(@PathVariable long ordId) {
        Order ord = ordService.getOrdDetails(ordId);
        ArrayList<ConfigOperation> opers = new ArrayList<ConfigOperation>();
        ord.getNeIds().forEach(neId -> {
            opers.add(service.getOperation(ord,neId));
        });
        neService.unassignOrder(ord);
        return opers;
    }
}