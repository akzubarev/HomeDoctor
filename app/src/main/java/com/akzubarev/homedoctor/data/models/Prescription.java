package com.akzubarev.homedoctor.data.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Prescription {
    private String name = "";
    private String length = "1 месяц";
    private User user;
    private ArrayList<Date> medications = new ArrayList<>();

}
