package com.acbenny.microservices.neservice.models;

public class Tag {
    int tagId;
    Order ord;

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public Order getOrd() {
        return ord;
    }

    public void setOrd(Order ord) {
        this.ord = ord;
    }

    public Tag(int tagId, Order ord) {
        this.tagId = tagId;
        this.ord = ord;
    }
}
