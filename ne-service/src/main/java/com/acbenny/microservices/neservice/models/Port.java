package com.acbenny.microservices.neservice.models;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Port implements Comparable<Port> {
    String port;
    
    @JsonInclude(Include.NON_EMPTY)
    Map<Integer,Tag> tags = new TreeMap<Integer,Tag>();

    public Port() {}

    public Port(final String port) {
        this.port = port;
    }

    public Port(final String port, Map<Integer,Tag> tags) {
        this.port = port;
        this.tags = tags;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    @Override
    public int compareTo(Port p) {
        String[] c1 = this.port.split("/");
        String[] c2 = p.port.split("/");
        int length = Math.min(c1.length, c2.length);
        for(int i = 0; i < length; i++) {
            int result = Integer.parseInt(c1[i]) - Integer.parseInt(c2[i]);
            if(result != 0) {
                return result;
            }
        }
        return c1.length - c2.length;
    }

    public Map<Integer, Tag> getTags() {
        return tags;
    }

    public void setTags(Map<Integer, Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.put(tag.getTagId(), tag);
    }
}