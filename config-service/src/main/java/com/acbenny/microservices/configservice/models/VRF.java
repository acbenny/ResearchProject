package com.acbenny.microservices.configservice.models;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class VRF {
    String vpnName;
    String vrfName;

    @JsonInclude(Include.NON_DEFAULT)
    boolean createDeleteCommunity;

    Map<Integer,String> interfaces = new TreeMap<>();

    public VRF(String vpnName, String vrfName) {
        this.vpnName = vpnName;
        this.vrfName = vrfName;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getVrfName() {
        return vrfName;
    }

    public void setVrfName(String vrfName) {
        this.vrfName = vrfName;
    }

    public Map<Integer, String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Map<Integer, String> interfaces) {
        this.interfaces = interfaces;
    }

    public void addInterface(int id, String serviceId) {
        this.interfaces.put(id, serviceId);
    }

    public boolean isCreateDeleteCommunity() {
        return createDeleteCommunity;
    }

    public void setCreateDeleteCommunity(boolean flag) {
        this.createDeleteCommunity = flag;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((vrfName == null) ? 0 : vrfName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VRF other = (VRF) obj;
        if (vrfName == null) {
            if (other.vrfName != null)
                return false;
        } else if (!vrfName.equals(other.vrfName))
            return false;
        return true;
    }
}
