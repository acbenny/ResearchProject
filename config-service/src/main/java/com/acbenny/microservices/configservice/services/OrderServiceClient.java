package com.acbenny.microservices.configservice.services;

import com.acbenny.microservices.configservice.models.Order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("order-service")
public interface OrderServiceClient {

	@GetMapping("/order/id/{ordId}")
	public Order getOrdDetails(@PathVariable long ordId);

}
