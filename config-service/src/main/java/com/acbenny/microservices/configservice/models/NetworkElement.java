package com.acbenny.microservices.configservice.models;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class NetworkElement {
    int neId;
    String model;

    Map<String,Object> ports = new LinkedHashMap<String,Object>();

    Set<VRF> vrfs = new HashSet<>();
    Map<Integer,String> filterIds = new TreeMap<>();

    public NetworkElement() {
    }

    public int getNeId() {
        return neId;
    }

    public void setNeId(int neId) {
        this.neId = neId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<String, Object> getPorts() {
        return ports;
    }

    public void setPorts(Map<String, Object> ports) {
        this.ports = ports;
    }

    public Set<VRF> getVrfs() {
        return vrfs;
    }

    public void setVrfs(Set<VRF> vrfs) {
        this.vrfs = vrfs;
    }

    public Map<Integer, String> getFilterIds() {
        return filterIds;
    }

    public void setFilterIds(Map<Integer, String> filterIds) {
        this.filterIds = filterIds;
    }

}