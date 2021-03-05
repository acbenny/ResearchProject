package com.acbenny.microservices.orderservice.repositories;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("nms-stub")
public interface NMSStubClient {
    
    @PostMapping("/actuator/restart")
	public void restart();
}