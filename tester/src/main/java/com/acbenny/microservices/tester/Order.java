package com.acbenny.microservices.tester;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Order {
    long orderId;
    String serviceId;
    String status;
    String vpnName;
    
    Set<Integer> neIds = new LinkedHashSet<>();

    public Order() {}

    public Order(long orderId, String serviceId) {
        this.orderId = orderId;
        this.serviceId = serviceId;
    }

    public Order(long orderId, String serviceId, String status, String vpnName, Set<Integer> neIds) {
        this.orderId = orderId;
        this.serviceId = serviceId;
        this.status = status;
        this.vpnName = vpnName;
        this.neIds = neIds;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Set<Integer> getNeIds() {
        return neIds;
    }

    public void setNeIds(Set<Integer> neIds) {
        this.neIds = neIds;
    }

    public void addNE(Integer neId) {
        neIds.add(neId);
    }

    public void removeNE(Integer neId) {
        neIds.remove(neId);
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
