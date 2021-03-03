package com.acbenny.microservices.orderservice.repositories;

import java.util.LinkedHashMap;

import com.acbenny.microservices.orderservice.models.Order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ne-service")
public interface NEServiceClient {
	
	@PostMapping("/ne/assign/route/{neId}")
	public LinkedHashMap<String, Object> route(@PathVariable long neId, @RequestBody Order ord);
	
	@PostMapping("/ne/assign/route/{neId}/{port}")
	public LinkedHashMap<String, Object> route(@PathVariable long neId, @PathVariable String port, @RequestBody Order ord);
	
	@GetMapping("/ne/{neId}/order/{ordId}")
	public LinkedHashMap<String, Object> getOrdDetails(@PathVariable int neId, @PathVariable long ordId);

	@PostMapping("/ne/unassign/route")
	public void unroute(@RequestBody LinkedHashMap<String, Object> neObject);

	@PostMapping("/ne/reset")
	public Object[] reset();
}
