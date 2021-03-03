package com.acbenny.microservices.neservice.models;

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

    Map<String,Port> ports = new LinkedHashMap<String,Port>();

    Set<VRF> vrfs = new HashSet<>();
    Map<Integer,String> filterIds = new TreeMap<>();

    public NetworkElement(){
        for (int i = 1;i<=10;i++){
            ports.put("1/1/"+i,new Port("1/1/"+i));
        }
    }

    public NetworkElement(String model) {
        this();
        this.model = model;
    }

    public NetworkElement(int neId, String model) {
        this.neId = neId;
        this.model = model;
	}

    public NetworkElement(int neId, String model, Map<String,Port> ports) {
        this.neId = neId;
        this.model = model;
        this.ports = ports;
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

    public Map<String,Port> getPorts() {
        return ports;
    }

    public void setPorts(Map<String, Port> ports) {
        this.ports = ports;
    }

    public void addPort(Port p) {
        this.ports.put(p.getPort(),p);
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

    public void addVrf(VRF vrf, int interfaceId, String serviceId) {
        if (vrfs.contains(vrf)) {
            for (VRF v : vrfs) {
                if (v.equals(vrf)){
                    vrf = v;
                }
            }
        }

        vrf.addInterface(interfaceId, serviceId);
        vrfs.add(vrf);
    }

    public void addFilter(int filterId, String serviceId) {
        filterIds.put(filterId, serviceId);
    }

}