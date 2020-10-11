package com.acbenny.microservices.neservice.controllers;

import com.acbenny.microservices.neservice.models.NetworkElement;
import com.acbenny.microservices.neservice.models.Order;
import com.acbenny.microservices.neservice.repositories.NeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ne")
public class NeControllers {

    @Autowired
    private NeRepository repo;

    @PostMapping("/create")
    public void createNE(@RequestBody NetworkElement ne){
        repo.createNE(ne);
    }
    
    @GetMapping("/")
    public NetworkElement[] getAllNEs() {
        return repo.getAllNEs();
    }

    @GetMapping("/{neId}")
    public NetworkElement getNE(@PathVariable int neId) {
        return repo.getNE(neId);
    }

    @GetMapping("/{neId}/{ordId}")
    public NetworkElement getNEOrder(@PathVariable int neId, @PathVariable long ordId) {
        return repo.getNEWithOrderFilter(neId,ordId);
    }

    @PostMapping("/assign/route/{neId}")
    public NetworkElement route(@PathVariable int neId, @RequestBody Order order) {
        return repo.route(neId, null, order);
    }

    @PostMapping("/assign/route/{neId}/{port}")
    public NetworkElement routeOnPort(@PathVariable int neId, @PathVariable String port, @RequestBody Order order) {
        return repo.route(neId, "1/1/"+port, order);
    }

    @PostMapping("/unassign/route")
    public void unroute(@RequestBody NetworkElement ne) {
        repo.unroute(ne);
    }
}