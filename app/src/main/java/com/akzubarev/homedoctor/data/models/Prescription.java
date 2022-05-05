package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

public class Prescription extends BaseModel {
    private String name = "";
    private String endDate = "02.05.2022";
    private String diagnosis = "";

    public Prescription(String name, String lenght) {
        this.name = name;
        this.endDate = lenght;
    }

    public Prescription() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

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
    @Exclude
    public String getDBID() {
        return getName();
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
