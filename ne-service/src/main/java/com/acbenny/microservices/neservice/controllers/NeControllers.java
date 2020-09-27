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

    @PostMapping("/assign/route/{neId}")
    public NetworkElement route(@PathVariable int neId,@RequestBody Order order) {
        return repo.route(neId, order);
    }

    @PostMapping("/unassign/route")
    public void unroute(@RequestBody NetworkElement ne) {
        repo.unroute(ne);
    }
}