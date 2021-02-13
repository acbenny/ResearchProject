package com.acbenny.microservices.nmsstub.models;

import java.util.Map;
import java.util.TreeMap;

public class Port {
    String port;
    Map<Integer,Object> tags = new TreeMap<Integer,Object>();

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map<Integer, Object> getTags() {
        return tags;
    }

    public void setTags(Map<Integer, Object> tags) {
        this.tags = tags;
    }

    
}
