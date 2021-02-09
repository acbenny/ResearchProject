package com.acbenny.microservices.orderservice.repositories;

import java.util.ArrayList;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("config-service")
public interface ConfigServiceClient {
    
    @GetMapping("/config/{ordId}")
	public ArrayList<Object> configOrder(@PathVariable long ordId);
}
