package com.akzubarev.homedoctor.data.models;

public abstract class BaseModel {
    protected String dbID = "";

    public BaseModel() {
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public abstract boolean validate();
}