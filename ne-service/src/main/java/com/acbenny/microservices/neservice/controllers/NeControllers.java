package com.acbenny.microservices.neservice.controllers;

import com.acbenny.microservices.neservice.models.NetworkElement;
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

    @PostMapping
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

}