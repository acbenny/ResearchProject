package com.acbenny.microservices.neservice.models;

public class TagAllocation {
    int tag;
    Order ord;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Order getOrd() {
        return ord;
    }

    public void setOrd(Order ord) {
        this.ord = ord;
    }

    public TagAllocation(int tag, Order ord) {
        this.tag = tag;
        this.ord = ord;
    }
}
