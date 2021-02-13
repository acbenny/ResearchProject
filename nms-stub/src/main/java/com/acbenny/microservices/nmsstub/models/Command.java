package com.acbenny.microservices.nmsstub.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Command {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    int neId;
    String vlanDescription;
    String vpnName;
    boolean createCommunity;
    String vrfName;
    int interfaceId;
    int filterId;
    String port;
    int tag;

    boolean valid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isCreateCommunity() {
        return createCommunity;
    }

    public void setCreateCommunity(boolean createCommunity) {
        this.createCommunity = createCommunity;
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

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Command() {
    }

    public Command(int neId, String vlanDescription, String vpnName, boolean createCommunity, String vrfName,
            int interfaceId, int filterId, String port, int tag) {
        this.neId = neId;
        this.vlanDescription = vlanDescription;
        this.vpnName = vpnName;
        this.createCommunity = createCommunity;
        this.vrfName = vrfName;
        this.interfaceId = interfaceId;
        this.filterId = filterId;
        this.port = port;
        this.tag = tag;
    }
}
