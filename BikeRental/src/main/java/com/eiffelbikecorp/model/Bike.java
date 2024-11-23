package com.eiffelbikecorp.model;

public class Bike {
    private String id;
    private String condition;
    private boolean isAvailable;

    // Constructor
    public Bike(String id, String condition, boolean isAvailable) {
        this.id = id;
        this.condition = condition;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
