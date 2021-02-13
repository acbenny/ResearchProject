package com.acbenny.microservices.configservice.controllers;

import java.util.ArrayList;

import com.acbenny.microservices.configservice.models.ConfigOperation;
import com.acbenny.microservices.configservice.services.ConfigService;
import com.acbenny.microservices.configservice.services.NmsStubClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigControllers {

    @Autowired
    private ConfigService service;

    @Autowired
    private NmsStubClient nmsStub;

    @GetMapping("/stub/config/{ordId}")
    public ArrayList<ConfigOperation> configOrdStub(@PathVariable long ordId) {
        return service.configOperations(ordId);
    }

    @GetMapping("/stub/deconfig/{ordId}")
    public ArrayList<ConfigOperation> deconfigOrdStub(@PathVariable long ordId) {
        return service.deconfigOperations(ordId);
    }

    @PostMapping("/config/{ordId}")
    public boolean configOrd(@PathVariable long ordId) {
        return nmsStub.config(service.configOperations(ordId));
    }
}