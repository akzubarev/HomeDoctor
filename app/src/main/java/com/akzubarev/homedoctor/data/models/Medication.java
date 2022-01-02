package com.akzubarev.homedoctor.data.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Medication {
    private String name = "";
    private String courceLength = "1 месяц";
    private int dailyFrequency = 1;
    private ArrayList<Date> consumptionTimes = new ArrayList<>();

    public Date nextConsumption() {
        Calendar now = Calendar.getInstance();
        Calendar comparing = Calendar.getInstance();
        for (Date time : consumptionTimes) {
            if (now.getTime().compareTo(time) < 0)
                return time;
        }
        return consumptionTimes.get(0);
    }

    public void setCourseLength(int quantity, String modifier) {
        courceLength = String.format(Locale.UK, "%d %s", quantity, modifier);
    }

    public void setBasicFrequency(int frequency) {
        dailyFrequency = frequency;
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < dailyFrequency; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, 10 + i * (12 / dailyFrequency));
            calendar.set(Calendar.MINUTE, 0);
            consumptionTimes.add(calendar.getTime());
        }
    }

    public void setFrequency(ArrayList<Date> consDates) {
        dailyFrequency = consDates.size();
        consumptionTimes = consDates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourceLength() {
        return courceLength;
    }

    public void setCourceLength(String courceLength) {
        this.courceLength = courceLength;
    }

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    public ArrayList<Date> getConsumptionTimes() {
        return consumptionTimes;
    }

    public void setConsumptionTimes(ArrayList<Date> consumptionTimes) {
        this.consumptionTimes = consumptionTimes;
    }

    public Medication() {
    }

    public Medication(String name, String courceLength, int dailyFrequency, ArrayList<Date> consumptionTimes) {
        this.name = name;
        this.courceLength = courceLength;
        this.dailyFrequency = dailyFrequency;
        this.consumptionTimes = consumptionTimes;
    }


    public String serialize() {
        String json = "";
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            json = objectMapper.writeValueAsString(this);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        return json;
    }

    public static Medication deserialize(String json) {
//        if (!json.isEmpty()) {
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                return objectMapper.readValue(json, Medication.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return null;
    }
}