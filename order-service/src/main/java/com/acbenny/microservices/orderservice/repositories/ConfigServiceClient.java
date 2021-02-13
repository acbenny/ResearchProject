package com.acbenny.microservices.orderservice.repositories;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("config-service")
public interface ConfigServiceClient {
    
    @GetMapping("/config/{ordId}")
	public boolean configOrder(@PathVariable long ordId);
}
