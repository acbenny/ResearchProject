package com.acbenny.microservices.neservice.models;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class NetworkElement {
    int neId;
    String model;

    @JsonInclude(Include.NON_EMPTY)
    Map<String,Port> ports = new LinkedHashMap<String,Port>();

    public NetworkElement(){
        for (int i = 1;i<=10;i++){
            ports.put("1/1/"+i,new Port("1/1/"+i));
        }
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
}