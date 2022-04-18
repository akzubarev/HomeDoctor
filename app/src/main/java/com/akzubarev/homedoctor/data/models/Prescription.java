package com.akzubarev.homedoctor.data.models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Prescription extends BaseModel {
    private String name = "";
    private String length = "1 месяц";
    private Map<String, String> consumptionTimes = new HashMap<>();

//    public Date nextConsumption() {
//        Calendar now = Calendar.getInstance();
//        Calendar comparing = Calendar.getInstance();
//        if (consumptionTimes.size() == 0)
//            return new Date(); //TODO: fix that
//
//        for (Date time : consumptionTimes) {
//            if (now.getTime().compareTo(time) < 0)
//                return time;
//        }
//        return consumptionTimes.get(0);
//    }
//
//    public void setCourseLength(int quantity, String modifier) {
//        courceLength = String.format(Locale.UK, "%d %s", quantity, modifier);
//    }
//
//    public void setBasicFrequency(int frequency) {
//        dailyFrequency = frequency;
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < dailyFrequency; i++) {
//            calendar.set(Calendar.HOUR_OF_DAY, 10 + i * (12 / dailyFrequency));
//            calendar.set(Calendar.MINUTE, 0);
//            consumptionTimes.add(calendar.getTime());
//        }
//    }
//public Medication nextConsumption() {
//    ArrayList<Date> dates = new ArrayList<>();
//    dates.add(Calendar.getInstance().getTime());
//    return new Medication("Парацетамол", "1 месяц", 1, dates);
//}

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("length", length);
        result.put("consumptionTimes", consumptionTimes);

        return result;
    }
}
