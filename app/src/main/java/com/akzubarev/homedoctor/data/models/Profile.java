package com.akzubarev.homedoctor.data.models;

public class Profile extends BaseModel {
    private String name = "";
    private String gender = "Другой";
    private String birthday = "";

    public Profile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean validate() {
        return !name.equals("") && !getName().equals("")
                && !birthday.equals("");
    }
}