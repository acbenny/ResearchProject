package com.acbenny.microservices.neservice.models;

public class NetworkElement {
    int neId;
    String model;

    public NetworkElement(int neId, String model) {
        this.neId = neId;
        this.model = model;
	}

	public NetworkElement() {
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
}