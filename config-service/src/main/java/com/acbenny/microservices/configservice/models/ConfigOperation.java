package com.acbenny.microservices.configservice.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class ConfigOperation {
    
    int neId;
    String vlanDescription;

    String vpnName;
    String vrfName;
    int interfaceId;
    boolean createCommunity;
    int filterId;

    Map<String,Object> ports;

    public int getNeId() {
        return neId;
    }

    public void setNeId(int neId) {
        this.neId = neId;
    }

    public String getVlanDescription() {
        return vlanDescription;
    }

    public void setVlanDescription(String vlanDescription) {
        this.vlanDescription = vlanDescription;
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

    public int getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(int interfaceId) {
        this.interfaceId = interfaceId;
    }

    public boolean isCreateCommunity() {
        return createCommunity;
    }

    public void setCreateCommunity(boolean createCommunity) {
        this.createCommunity = createCommunity;
    }

    public Map<String, Object> getPorts() {
        return ports;
    }

    public void setPorts(Map<String, Object> ports) {
        this.ports = ports;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

}
