package com.acbenny.microservices.orderservice.repositories;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("config-service")
public interface ConfigServiceClient {
    
    @PostMapping("/config/{ordId}")
	public boolean configOrder(@PathVariable long ordId);
}
