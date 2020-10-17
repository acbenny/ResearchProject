package com.acbenny.microservices.orderservice.controllers;

import java.util.Set;

import com.acbenny.microservices.orderservice.models.Order;
import com.acbenny.microservices.orderservice.repositories.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderControllers {

    @Autowired
    private OrderRepository repo;

    @PostMapping("/create")
    public void createOrdre(@RequestBody Order ord){
        repo.createOrder(ord);
    }

    @PostMapping("/route/{serviceId}")
    public void routeOrder(@PathVariable String serviceId,@RequestBody Set<String> neIDs){
        repo.routeOrder(serviceId,neIDs);
    }

    @PostMapping("/unroute/{serviceId}")
    public void unrouteOrder(@PathVariable String serviceId,@RequestBody Set<Integer> neIDs){
        repo.unrouteOrder(serviceId,neIDs);
    }

    @GetMapping("/")
    public Order[] getAllOrders() {
        return repo.getAllOrders();
    }

    @GetMapping("/id/{ordId}")
    public Order getOrder(@PathVariable long ordId) {
        return repo.getOrd(ordId);
    }

    @GetMapping("/{serviceId}")
    public Order[] getAllServiceOrders(@PathVariable String serviceId) {
        return repo.getAllServiceOrders(serviceId);
    }

    @GetMapping("/{serviceId}/latest")
    public Order getLatestServiceOrder(@PathVariable String serviceId) {
        return repo.getLatestServiceOrder(serviceId);
    }
}