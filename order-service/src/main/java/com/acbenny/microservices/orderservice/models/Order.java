package com.acbenny.microservices.orderservice.models;

public class Order {
    long orderId;
    String serviceId;

    public Order() {}

    public Order(long orderId, String serviceId) {
        this.orderId = orderId;
        this.serviceId = serviceId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
