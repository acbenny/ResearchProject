package com.acbenny.microservices.configservice.services;

import com.acbenny.microservices.configservice.models.NetworkElement;
import com.acbenny.microservices.configservice.models.Order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ne-service")
public interface NEServiceClient {
	
	@GetMapping("/ne/{neId}/order/{ordId}")
	public NetworkElement getOrdDetails(@PathVariable int neId, @PathVariable long ordId);

	@PostMapping("/ne/assign/vrf/{neId}")
	public NetworkElement assignVRF(@PathVariable long neId, @RequestBody Order ord);

	@PostMapping("/ne/unassign/order")
	public NetworkElement unassignOrder(@RequestBody Order ord);
}
